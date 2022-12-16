select
    from_unixtime(a.order_time, '%Y-%m-%d') as order_time,
    a.order_num as order_num,
    concat(cci.address_prefix, cci.address_suffix) address
from hm_erp_order a
         left join hm_erp_client_info cci on cci.client_id = a.client_id
where a.order_time between 1577808000 and 1671120000
  and a.del_flag = 0
  and a.order_status = 0
group by a.order_num
order by a.order_time