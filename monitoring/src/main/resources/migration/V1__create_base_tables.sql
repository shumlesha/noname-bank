CREATE TABLE trace_logs (
       id BIGSERIAL PRIMARY KEY,
       request_id VARCHAR(255) NOT NULL,
       service_name VARCHAR(255) NOT NULL,
       endpoint VARCHAR(255) NOT NULL,
       response_time_millis BIGINT,
       timestamp BIGINT NOT NULL
);

CREATE TABLE metrics (
       id BIGSERIAL PRIMARY KEY,
       request_id VARCHAR(255) NOT NULL,
       metric_name VARCHAR(255) NOT NULL,
       timestamp BIGINT NOT NULL
);