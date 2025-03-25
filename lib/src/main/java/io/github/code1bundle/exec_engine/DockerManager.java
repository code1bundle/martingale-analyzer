package io.github.code1bundle.exec_engine;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.api.model.WaitResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DockerManager {
    private final DockerClient dockerClient;

    public DockerManager() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375")
                .build();

        this.dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(new ApacheDockerHttpClient.Builder()
                        .dockerHost(config.getDockerHost())
                        .build())
                .build();
    }

    public void buildImage(String absPath, String imageName) {
        dockerClient.buildImageCmd()
                .withDockerfile(new File(absPath))
                .withTag(imageName)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
    }

    public String launchContainer(String imageName, List<String> commands, List<String> envVars) throws InterruptedException {
        CreateContainerResponse containerResponse = dockerClient.createContainerCmd(imageName)
                .withCmd(commands)
                .withEnv(envVars)
                .withAttachStdout(false)
                .withAttachStderr(false)
                .exec();

        dockerClient.startContainerCmd(containerResponse.getId()).exec();

        StringBuilder output = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        dockerClient.logContainerCmd(containerResponse.getId())
                .withStdOut(true)
                .withStdErr(false)
                .withFollowStream(true)
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        if (frame.getStreamType() == StreamType.STDOUT) {
                            output.append(new String(frame.getPayload()));
                        }
                    }

                    @Override
                    public void onComplete() {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.err.println("Error during container execution: " + throwable.getMessage());
                        throwable.printStackTrace();
                        latch.countDown();
                    }
                });

        Integer statusCode = dockerClient.waitContainerCmd(containerResponse.getId())
                .exec(new WaitContainerResultCallback())
                .awaitStatusCode(30, TimeUnit.SECONDS);

        if (statusCode != 0) {
            System.err.println("Container exited with non-zero status code: " + statusCode);
        }

        if (!latch.await(30, TimeUnit.SECONDS)) {
            System.err.println("Container log streaming timed out!");
        }

        try {
            dockerClient.stopContainerCmd(containerResponse.getId()).exec();
        } catch (com.github.dockerjava.api.exception.NotModifiedException e) {
            // just ignoring cuz container already closed
        }
        try {
            dockerClient.removeContainerCmd(containerResponse.getId()).exec();
        } catch (Exception e) {
            // just ignoring cuz container already removed
        }

        return output.toString();
    }


    // Inner class for waiting on container exit
    private static class WaitContainerResultCallback extends ResultCallback.Adapter<WaitResponse> {
        private Integer statusCode;

        @Override
        public void onNext(com.github.dockerjava.api.model.WaitResponse waitResponse) {
            this.statusCode = waitResponse.getStatusCode();
        }

        public Integer awaitStatusCode(long timeout, TimeUnit timeUnit) throws InterruptedException {
            if (!awaitCompletion(timeout, timeUnit)) {
                throw new InterruptedException("Timeout waiting for container to exit");
            }
            return statusCode;
        }
    }
}