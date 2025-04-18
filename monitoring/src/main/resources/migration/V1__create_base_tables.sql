CREATE TABLE trace_logs (
       id BIGSERIAL PRIMARY KEY,
       request_id VARCHAR(255) NOT NULL,
       service_name VARCHAR(255) NOT NULL,
       endpoint VARCHAR(255) NOT NULL,
       response_time_millis BIGINT,
       timestamp BIGINT NOT NULL,
       message VARCHAR(255)
);

CREATE TABLE metrics (
       id BIGSERIAL PRIMARY KEY,
       metric_name VARCHAR(255) NOT NULL,
       timestamp BIGINT NOT NULL
);