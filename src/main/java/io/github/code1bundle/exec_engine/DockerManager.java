package io.github.code1bundle.exec_engine;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.DockerClientBuilder;

import java.io.Closeable;
import java.util.List;

public class DockerManager {
    private final DockerClient dockerClient;

    public DockerManager() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    // returns Image ID
    public String buildImage(String dockerfilePath, String nameTag) {
        return dockerClient.buildImageCmd()
                .withDockerfilePath(dockerfilePath)
                .withTag(nameTag)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
    }

    // returns Container logs as a String
    public String launchContainer(String imageName, List<String> commands, List<String> envVars) {
        CreateContainerResponse containerResponse = dockerClient.createContainerCmd(imageName)
                .withCmd(commands)
                .withEnv(envVars)
                .exec();
        StringBuilder output = new StringBuilder();
        dockerClient.logContainerCmd(containerResponse.getId())
                .withStdOut(true)
                .withStdErr(true)
                .exec(new ResultCallback<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        output.append(new String(frame.getPayload()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete() {}

                    @Override
                    public void close() {}

                    @Override
                    public void onStart(Closeable closeable) {}
                });

        dockerClient.stopContainerCmd(containerResponse.getId()).exec();
        dockerClient.removeContainerCmd(containerResponse.getId()).exec();

        return output.toString();
    }
}
