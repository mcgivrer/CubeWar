FROM gitpod/workspace-full-vnc
RUN bash -c "sudo apt-get update && \
    sudo apt-get install -y libgtk-3-dev && \
    sudo rm -rf /var/lib/apt/lists/* && \
    curl -s \"https://get.sdkman.io\" | bash && \
    . /home/gitpod/.sdkman/bin/sdkman-init.sh &&\
    source \"$HOME/.sdkman/bin/sdkman-init.sh\" && \
    sdk update && \ 
    sdk env install && \
    sdk env use"
