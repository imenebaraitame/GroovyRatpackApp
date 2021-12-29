FROM gitpod/workspace-full

ENV TESSDATA_PREFIX=/usr/share/tesseract-ocr/4.00/tessdata

# Install dart
USER root

RUN apt-get update -y && \
    apt-get install libtesseract-dev -y && \
    apt-get install tesseract-ocr-eng -y && \
    apt-get install tesseract-ocr-ara -y && \
    apt-get install tesseract-ocr-fra -y && \
    apt-get install imagemagick -y

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java 8.0.292.j9-adpt"    
RUN wget https://github.com/tesseract-ocr/tessdata/blob/master/eng.traineddata
RUN wget https://github.com/tesseract-ocr/tessdata/blob/master/ara.traineddata
RUN wget https://github.com/tesseract-ocr/tessdata/blob/master/fra.traineddata

USER gitpod

# add executables to PATH
RUN echo 'export PATH=${TESSDATA_PREFIX}:$PATH' >>~/.bashrc