#sql("getRouteItemList")
  SELECT
	id,
	route_id,
	channel_code,
	channel_interface_code,
	settle_or_merchant_acc_id,
	[level]
  FROM
    handle_route_setting_item
  WHERE
    route_id = ?
#end

#sql("deleteRouteItemByRouteId")
  DELETE
  FROM
    handle_route_setting_item
  WHERE
    route_id = ?
#end