set @nextround=4, @prevround=3;

-- transition codes to next round
update codes set round_id=@nextround where code not in (select code from users);

-- copy data to next round
insert into  global (id, round_id, user_id, name, create_date, last_modification, origin_round)
select @nextround*id, @nextround as round_id, user_id, name, create_date, last_modification, round_id as origin
from global where round_id=@prevround;

insert into part (id, user_id, round_id, template_part_id, name, body, create_date, last_modification)
select id*@nextround, user_id, @nextround, template_part_id, name, body, create_date, last_modification
from part where round_id=@prevround;

insert into global_parts (global_id, part_id)
select global_id*@nextround, part_id*@nextround
from global_parts gp inner join global g on gp.global_id=g.id
where g.round_id=@prevround;