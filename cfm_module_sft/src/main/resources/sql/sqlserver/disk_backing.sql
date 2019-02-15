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
   tmp_status = 1,
   paybankaccno = ?,
   paybankcode = ? ,
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
   from
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
   from
   ebs_origin_pay_data
   where
   id
   in
   (
   select  origin_id from pay_batch_detail where child_batchno = ?
   )
#end


