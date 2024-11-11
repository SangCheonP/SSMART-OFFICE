# Jenkins Official Image
FROM jenkins/jenkins:lts

USER root

# Install Docker
RUN apt-get update && \
    apt-get install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common && \
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - && \
    add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable" && \
    apt-get update && \
    apt-get install -y docker-ce docker-ce-cli containerd.io

# Install Docker Compose
RUN curl -L "https://github.com/docker/compose/releases/download/v2.3.3/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# Set up Jenkins user and permissions
RUN usermod -aG docker jenkins

# Jenkins 실행 포트를 7070으로 변경
ENV JENKINS_OPTS="--httpPort=7070"

# Expose Ports
EXPOSE 7070 50000

# Switch back to jenkins user
USER jenkins
