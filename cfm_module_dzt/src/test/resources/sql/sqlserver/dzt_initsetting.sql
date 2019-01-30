#sql("findByAccId")
	select 
		*
	from 
		check_voucher_initdata 
	where
		acc_id = #(acc_id)
#end

#sql("upd")
	update
		check_voucher_initdata
	set 
		balance = #(balance),
		year = #(year),
		month = #(month)
	where
		acc_id = #(acc_id)
#end

#sql("delSon")
	delete from
		check_voucher_initdata_item
	where
		base_id = #(base_id)
#end

#sql("enable")
	update
		check_voucher_initdata
	set 
		balance = #(balance),
		year = #(year),
		month = #(month),
		is_enabled = 1
	where
		acc_id = #(acc_id)
#end

#sql("finaItems")
	select 
		s.* 
	from 
		check_voucher_initdata_item s
	inner join
		check_voucher_initdata p
	on p.id = s.base_id
	where p.acc_id = #(acc_id) and p.year = #(year) and p.month = #(month) 
#end

#sql("findByPage")
	select 
		p.*
		,(select count(0) from check_voucher_initdata_item s where p.id=s.base_id and s.data_type=1) as enter_fail_achieve
		,(select count(0) from check_voucher_initdata_item s where p.id=s.base_id and s.data_type=2) as bank_fail_achieve
	from 
		check_voucher_initdata p
	order by id ASC
#end

#sql("addBalance")
	insert into 
		check_voucher_acc_balance(acc_id, acc_no, acc_name, year, month, balance )
		select 
			acc_id, acc_no, acc_name, year, month, balance 
		from 
			check_voucher_initdata p 
		where p.acc_id=#(acc_id) and p.year=#(year) and p.month=#(month)
#end