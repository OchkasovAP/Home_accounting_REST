DELETE FROM users WHERE id>2;

UPDATE users SET 
login='admin', 
password='$2a$10$h9bRhb5TBIK30J4KDiJefOpZOuTD3H/KI0Xb5eojXEq3VLlRwn30e',
role_id=1
WHERE id=1;

UPDATE users set 
login='user', 
password='$2a$10$h9bRhb5TBIK30J4KDiJefOpZOuTD3H/KI0Xb5eojXEq3VLlRwn30e',
role_id=2
where id=2;
