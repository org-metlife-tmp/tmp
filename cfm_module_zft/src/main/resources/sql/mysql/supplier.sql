#sql("findSupplierAccInfoList")
SELECT
	supp.id,
	supp.acc_no,
	supp.acc_name,
	supp.cnaps_code,
	supp.bank_name,
	supp.province,
	supp.city,
	supp.address,
	supp.create_by,
	supp.create_on,
	supp.update_by,
	supp.update_on,
	supp.persist_version,
	supp.`type`,
	supp.curr_id,
	supp.attach_id,
	supp.delete_flag,
	bank.bank_type
FROM
	supplier_acc_info supp,all_bank_info bank
WHERE 1=1 and supp.cnaps_code = bank.cnaps_code
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("acc_no".equals(x.key) || "acc_name".equals(x.key))
          supp.#(x.key) like concat('%', #para(x.value), '%')
        #elseif("delete_flag".equals(x.key))
          supp.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
#end

#sql("findSupplierByAccNo")
  SELECT
	id,
	acc_no,
	acc_name,
	cnaps_code,
	bank_name,
	province,
	city,
	address,
	create_by,
	create_on,
	update_by,
	update_on,
	persist_version,
	`type`,
	curr_id,
	attach_id,
	delete_flag
FROM
	supplier_acc_info
WHERE delete_flag = ?
AND acc_no = ?
#end