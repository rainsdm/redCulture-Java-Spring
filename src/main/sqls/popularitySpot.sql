create view spots_visit_num as
select s.id as spot_id, s.name as spot_name, count(*) as visit_num
from spots s
         join records r on s.id = r.spot_id
group by s.id
order by visit_num desc;