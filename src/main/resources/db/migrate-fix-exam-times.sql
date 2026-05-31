-- Fix exam times to be relative to current time so students always have available exams
UPDATE exam SET 
    start_time = NOW() - INTERVAL 1 DAY,
    end_time = NOW() + INTERVAL 7 DAY
WHERE id = 1 AND status = 'published';

UPDATE exam SET 
    start_time = NOW() - INTERVAL 1 DAY,
    end_time = NOW() + INTERVAL 3 DAY
WHERE id = 2 AND status = 'published';

UPDATE exam SET 
    start_time = NOW() - INTERVAL 1 DAY,
    end_time = NOW() + INTERVAL 5 DAY
WHERE id = 3 AND status = 'published';

UPDATE exam SET 
    start_time = NOW() - INTERVAL 1 DAY,
    end_time = NOW() + INTERVAL 10 DAY
WHERE id = 4 AND status = 'published';
