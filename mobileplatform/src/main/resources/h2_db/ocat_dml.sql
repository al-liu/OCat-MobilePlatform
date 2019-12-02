

INSERT INTO resource (id, version_name, version_code, data)
VALUES
    (232434234234, '1.0.0', 1, RAWTOHEX('{}')),
    (278423784236, '1.0.1', 2, RAWTOHEX('{}')),
    (278423784237, '1.0.2', 3, RAWTOHEX('{}')),
    (278423784238, '1.0.3', 4, RAWTOHEX('{}'));

INSERT INTO patch (id, new_version, old_version, data, url)
VALUES
    (193209302390, '1.0.2', '1.0.0', RAWTOHEX('{}'), 'http://bing.com'),
    (293204902349, '1.0.2', '1.0.1', RAWTOHEX('{}'), 'http://google.com');
