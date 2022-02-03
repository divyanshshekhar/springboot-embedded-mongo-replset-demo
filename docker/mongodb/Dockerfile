FROM mongo:5.0.5-focal
RUN echo "rs.initiate();" > /docker-entrypoint-initdb.d/replica-init.js
CMD ["--replSet", "rs0"]