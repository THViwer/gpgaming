select
m.boss_id,
m.client_id,
x.agent_id superior_agent_id,
m.agent_id,
m.sale_id,
m.sale_scope,
m.id,
m.market_id,
m.level_id,
m.username,
IFNULL(d.total_deposit,0) total_deposit,
IFNULL(d.deposit_count,0) deposit_count,
IFNULL(p.third_pay_amount,0) third_pay_amount,
IFNULL(p.third_pay_count,0) third_pay_count,
IFNULL(a.artificial_amount, 0) artificial_amount,
IFNULL(a.artificial_count, 0) artificial_count,
IFNULL(w.total_withdraw,0) total_withdraw,
IFNULL(w.withdraw_count,0) withdraw_count,
IFNULL(t1.transfer_out, 0) transfer_out,
IFNULL(t1.promotion_amount, 0) promotion_amount,
IFNULL(t1.requirement_bet, 0) requirement_bet,
IFNULL(st.slot_requirement_bet, 0) slot_requirement_bet,
IFNULL(lt.live_requirement_bet, 0) live_requirement_bet,
IFNULL(spt.sport_requirement_bet, 0) sport_requirement_bet,
IFNULL(ft.fish_requirement_bet, 0) fish_requirement_bet,
IFNULL(t2.transfer_in, 0) transfer_in
from member m
left join (
select member_id, sum(money) total_deposit, count(*) deposit_count from deposit d
where d.created_time > '2021-03-20' and d.created_time < '2021-03-21' and d.state = 'Successful' group by member_id
) d on m.id = d.member_id
left join (
select member_id, sum(amount) third_pay_amount, count(*) third_pay_count from pay_order p
where p.created_time > '2021-03-20' and p.created_time < '2021-03-21' and p.state = 'Successful' group by member_id
) p on m.id = p.member_id
left join (
select member_id, sum(money) artificial_amount, count(*) artificial_count from artificial_order a
where a.created_time > '2021-03-20' and a.created_time < '2021-03-21' group by member_id
) a on m.id = a.member_id
left join (
select member_id, sum(money) total_withdraw, count(*) withdraw_count from withdraw w
where w.created_time > '2021-03-20' and w.created_time < '2021-03-21'  and w.state = 'Successful' group  by member_id
) w on m.id = w.member_id
left join (
select t.member_id, sum(money) transfer_out, sum(promotion_amount) promotion_amount, sum(requirement_bet) requirement_bet from transfer_order t
where t.created_time > '2021-03-20' and t.created_time < '2021-03-21' and state = 'Successful' and t.`from` = 'Center' group by member_id
) t1 on m.id = t1.member_id

left join (
select member_id, sum(requirement_bet) as slot_requirement_bet from transfer_order where  state = 'Successful' and created_time > '2020-03-21' and created_time < '2021-03-21'
and `to` in ('Joker', 'Kiss918', 'Pussy888', 'Mega', 'Pragmatic', 'SpadeGaming', 'TTG', 'MicroGaming', 'PlaytechSlot', 'PNG', 'GamePlay', 'SimplePlay', 'AsiaGamingSlot')
(and unlock_date = '2020-03-20' or `lock` = true)
group by member_id
) st on m.id = st.member_id
left join (
select member_id, sum(requirement_bet) as live_requirement_bet from transfer_order where  state = 'Successful' and created_time > '2020-03-21' and created_time < '2021-03-21'
and `to` in ('CT', 'DreamGaming', 'Evolution', 'GoldDeluxe', 'SexyGaming', 'Fgg', 'AllBet', 'SaGaming', 'AsiaGamingLive', 'MicroGamingLive', 'PlaytechLive', 'EBet')
(and unlock_date = '2020-03-20' or `lock` = true)
group by member_id
) lt on m.id = lt.member_id
left join (
select member_id, sum(requirement_bet) as sport_requirement_bet from transfer_order where  state = 'Successful' and created_time > '2020-03-21' and created_time < '2021-03-21'
and `to` in ('Lbc', 'Bcs', 'CMD')
(and unlock_date = '2020-03-20' or `lock` = true)
group by member_id
) spt on m.id = spt.member_id
left join (
select member_id, sum(requirement_bet) as fish_requirement_bet from transfer_order where  state = 'Successful' and created_time > '2020-03-21' and created_time < '2021-03-21'
and `to` in ('Lbc', 'Bcs', 'CMD')
(and unlock_date = '2020-03-20' or `lock` = true)
group by member_id
) ft on m.id = ft.member_id

left join (
select t.member_id, sum(money) transfer_in from transfer_order t
where t.created_time > '2021-03-20' and t.created_time < '2021-03-21' and state = 'Successful' and t.`to` = 'Center' group by member_id
) t2 on m.id = t2.member_id

left join member x on x.id = m.agent_id
where m.role  = 'Member' and m.id = 12805;