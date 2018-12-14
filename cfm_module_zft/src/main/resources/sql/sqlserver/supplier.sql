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
	supp.[type],
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
          supp.#(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("delete_flag".equals(x.key))
          supp.#(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by supp.id
#end

#sql("findSupplierByAccNo")
  SELECT
	acc.id,
	acc.acc_no,
	acc.acc_name,
	acc.cnaps_code,
	acc.bank_name,
	acc.province,
	acc.city,
	acc.address,
	acc.create_by,
	acc.create_on,
	acc.update_by,
	acc.update_on,
	acc.persist_version,
	acc.[type],
	acc.curr_id,
	acc.attach_id,
	acc.delete_flag,
	curr.name as curr_name,
	curr.iso_code
FROM
	supplier_acc_info acc
	left join currency curr
	on  curr.id = acc.curr_id
WHERE acc.delete_flag = ?
AND acc.acc_no = ?
#end


#sql("querySupplier")
  SELECT *  from supplier_acc_info
  where  delete_flag = 0
  and  acc_no = ?
#end


#sql("findSupplierInfoList")
SELECT
	*
FROM
	supplier_acc_info
WHERE 1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("acc_no".equals(x.key) || "acc_name".equals(x.key))
          #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #elseif("delete_flag".equals(x.key))
          #(x.key) = #para(x.value)
        #end
      #end
    #end
  #end
  order by id desc
#end