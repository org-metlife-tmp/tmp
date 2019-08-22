#sql("findElectronicBillList")
SELECT
	id,
	channel_code,
	eb_type,
	amount,
	eb_date,
	field_1,
	field_2,
	field_3,
	field_4,
	field_5,
	field_6,
	field_7,
	field_8,
	field_9,
	field_10,
	field_11,
	field_12,
	field_13,
	field_14,
	field_15,
	field_16,
	field_17,
	field_18,
	field_19,
	field_20,
	field_21,
	field_22,
	field_23,
	field_24,
	field_25,
	field_26,
	field_27,
	field_28,
	field_29,
	field_30,
	field_31,
	field_32,
	field_33,
	field_34,
	field_35,
	field_36,
	field_37,
	field_38,
	field_39,
	field_40,
	field_41,
	field_42,
	field_43,
	field_44,
	field_45,
	field_46,
	field_47,
	field_48,
	field_49,
	field_50
FROM
	electronic_bill
WHERE 1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
        #if("min".equals(x.key))
          amount >= #para(x.value)
        #elseif("max".equals(x.key))
          amount <= #para(x.value)
        #else
          #(x.key) like convert(varchar(5),'%')+convert(varchar(255),#para(x.value))+convert(varchar(5),'%')
        #end
      #end
    #end
  #end
  order by id desc
#end

#sql("findElectronicType")
SELECT
	uuid,
	channel_code,
	eb_type,
	eb_type_desc
FROM
	electronic_bill_type
WHERE 1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
          #(x.key) = #para(x.value)
      #end
    #end
  #end
#end

#sql("findElectronicBillTemplate")
SELECT
	uuid,
	origin_fd,
	origin_fd_desc,
	ref_fd
FROM
	electronic_bill_template
WHERE 1=1
  #if(map != null)
    #for(x : map)
      #if(x.value&&x.value!="")
        AND
          #(x.key) = #para(x.value)
      #end
    #end
  #end
order by uuid
#end