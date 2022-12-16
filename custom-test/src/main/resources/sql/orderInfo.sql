select
    from_unixtime(a.order_time, '%Y-%m-%d') as order_time,
    a.order_num as order_num,
    concat(cci.address_prefix, cci.address_suffix) address
from hm_erp_order a
         left join hm_erp_order_express_info ex on ex.order_num = a.order_num
         left join hm_erp_client_info cci on cci.client_id = a.client_id
where a.order_time between 1577808000 and 1671120000
  and a.del_flag = 0
  and a.order_status = 0
  and (ex.express_num is not null and ex.express_num != '')
  and exists (select 1 from hm_erp_order_express_process oep where oep.order_num = a.order_num and oep.pickup_status = 1)
group by a.order_num
order by a.order_time