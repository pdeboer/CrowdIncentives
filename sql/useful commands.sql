-- transition codes to next round
update codes set round_id=4 where code not in (select code from users);