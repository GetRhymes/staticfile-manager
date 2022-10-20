FROM docker.io/minio/minio:latest

COPY --from=docker.io/minio/mc:latest /usr/bin/mc /usr/bin/mc
RUN mkdir /buckets
RUN minio server /buckets &

CMD ["minio", "server", "/buckets", "--address", ":9000", "--console-address", ":9001"]