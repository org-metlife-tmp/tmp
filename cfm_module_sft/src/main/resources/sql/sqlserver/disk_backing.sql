#sql("updateLaOriginData")
   update
   la_origin_pay_data
   set 
   tmp_status = 1
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end


#sql("updateEbsOriginData")
   update
   ebs_origin_pay_data
   set 
   tmp_status = 1
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end


#sql("selectLaOriginData")
   select
    *
   la_origin_pay_data
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end

#sql("selectEbsOriginData")
   select
    *
   ebs_origin_pay_data
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end


