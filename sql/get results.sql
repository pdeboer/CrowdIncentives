select gu.username as global_writer, gu.code as global_code,  g.name as global_name,
      pu.username as part_writer, pu.code as part_code,  p.name as part_name, p.body as part_body
from global g inner join global_parts gp on g.id = gp.global_id 
	inner join part p on gp.part_id = p.id
    inner join users gu on g.user_id = gu.id
    inner join users pu on p.user_id = pu.id
where g.`round_id`=4;


select pu.username as part_writer, pu.code as part_code,  p.name as part_name, p.body as part_body
from part p inner join users pu on p.user_id=pu.id
where p.round_id=4 and
  p.id not in (select part_id from global_parts gp inner join part p on gp.part_id=p.id and round_id=4);