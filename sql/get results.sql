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



  select gu.username as global_writer, gu.code as global_code,  g.name as global_name,
      pu1.username as part1_writer, pu1.code as part1_code,  p1.name as part1_name, p1.body as part1_body,
      pu2.username as part2_writer, pu2.code as part2_code,  p2.name as part2_name, p2.body as part2_body,
      pu3.username as part3_writer, pu3.code as part3_code,  p3.name as part3_name, p3.body as part3_body
from global g left join global_parts gp1 on g.id = gp1.global_id
	inner join part p1 on gp1.part_id = p1.id and p1.`template_part_id`=4
    left join users pu1 on p1.user_id = pu1.id
    left join global_parts gp2 on g.id = gp2.global_id
	inner join part p2 on gp2.part_id = p2.id and p2.`template_part_id`=5
    left join users pu2 on p2.user_id = pu2.id
    left join global_parts gp3 on g.id = gp3.global_id
	inner join part p3 on gp3.part_id = p3.id and p3.`template_part_id`=6
    left join users pu3 on p3.user_id = pu3.id
    inner join users gu on g.user_id = gu.id
where g.`round_id`=4;
