FROM ruby:3.2-slim
WORKDIR /app

COPY Gemfile Gemfile.lock ./
RUN bundle install --quiet

COPY . .