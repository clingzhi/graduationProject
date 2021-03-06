package cc.ysf.dx.service.Impl;

import cc.ysf.dx.dao.HotelOrderDao;
import cc.ysf.dx.dao.HotelRoomDao;
import cc.ysf.dx.pojo.entity.HotelRoom;
import cc.ysf.dx.pojo.vo.SearchHotelRoomVo;
import cc.ysf.dx.pojo.vo.ValidateRoomStoreVO;
import cc.ysf.dx.service.HotelRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * >>> 爱旅行-- 业务层接口实现类-- 酒店房间信息
 */
@Service("hotelRoomService")
@Transactional
public class HotelRoomServiceImpl implements HotelRoomService {
	@Autowired
	private HotelRoomDao hotelRoomDao;
	@Autowired
	private HotelOrderDao hotelOrderDao;

	/**
	 * >>>  根据查询条件去查询酒店房间列表-此刻可以预定的房间列表
	 * @param searchHotelRoomVo
	 * @return
	 * @throws Exception
	 */
	public List<HotelRoom> queryHotelRoomByHotel(SearchHotelRoomVo searchHotelRoomVo) throws Exception {
		List<HotelRoom> hotelRoomList = new ArrayList<HotelRoom>();

		//1,根据酒店ID 查询该酒店的所有房间列表
		HotelRoom query = new HotelRoom();
		query.setHotelId(searchHotelRoomVo.getHotelId());
		List<HotelRoom> allHotelRoomList = hotelRoomDao.findHotelRoomList(query);

		if(allHotelRoomList != null && allHotelRoomList.size()>0){
			//2, 查询临时库存---循环遍历该列表，根据房间id和当前时间查询临时库存数量
			for (HotelRoom hotelRoom : allHotelRoomList) {
				//封装查询数据
				Map<String,Object> queryMap = new HashMap<String, Object>();
				queryMap.put("roomId", query.getId());
				queryMap.put("beginDate",searchHotelRoomVo.getStartDate());
				Integer store = hotelRoomDao.queryTempStore(queryMap);

				if(store == null){
					//3，如果临时库存不存在 ，就查询总库存数量
					queryMap.put("productId", hotelRoom.getId());
					store = hotelRoomDao.queryTotalStore(queryMap);
				}
				//计算可用库存，如果大于零
				if(store != null && store > 0){
					//在计算此时，订单表中下单和已经支付的订单使用的库存
					Map<String,Object> orderQueryMap = new HashMap<String, Object>();
					orderQueryMap.put("roomId",hotelRoom.getId());
					orderQueryMap.put("startDate", searchHotelRoomVo.getStartDate());
					orderQueryMap.put("endDate", searchHotelRoomVo.getEndDate());
					Integer orderRoomCount = hotelOrderDao.findOrderRoomCountByQuery(orderQueryMap);
					// 使用库存-订单输入，如果大于0则说明该房间可用，那么加入最终的结果列表
					if (store - orderRoomCount > 0) {
						hotelRoomList.add(hotelRoom);
					}
				}
			}
		}
		return hotelRoomList;
	}

	/**
	 * >>> 下单时，再次查询临时库存
	 * @param validateRoomStoreVO
	 * @return
	 */
	public int getHotelRoomByDate(ValidateRoomStoreVO validateRoomStoreVO) throws Exception {
		//查询临时库存 根据返回值确定存入数据类型
		Map<String,Object> queryMap = new HashMap<String, Object>();
		queryMap.put("roomId", validateRoomStoreVO.getRoomId());
		queryMap.put("beginDate", validateRoomStoreVO.getCheckOutDate());

		Integer store = hotelRoomDao.queryTempStore(queryMap);

		if(store == null){
			//再用商品ID（）查询总库存
			queryMap.put("productId", validateRoomStoreVO.getRoomId());
			store = hotelRoomDao.queryTotalStore(queryMap);
		}

		if (queryMap != null && queryMap.size()>0) {
			//继续排除此时订单中占用的库存
			Map<String,Object> orderStoreMap = new HashMap<String, Object>();
			orderStoreMap.put("roomId", validateRoomStoreVO.getRoomId());
			orderStoreMap.put("startDate", validateRoomStoreVO.getCheckInDate());
			orderStoreMap.put("endDate",validateRoomStoreVO.getCheckOutDate());

			Integer orderStore = hotelOrderDao.findOrderRoomCountByQuery(orderStoreMap);

			if (store - orderStore >0) {
				return store-orderStore;
			}
		}
		return  0;
	}

	/**
	 * >>>  查询就点房间用房间ID查
	 * @param roomId
	 * @return
	 */
	public HotelRoom getHotelRoomById(Long roomId)throws Exception {
		HotelRoom hotelRoom = new HotelRoom();
		hotelRoom.setId(roomId);

		List<HotelRoom> hotelRoomList = hotelRoomDao.findHotelRoomList(hotelRoom);
		if (hotelRoomList != null &&  hotelRoomList.size() > 0){
			return hotelRoomList.get(0);
		}
		return new HotelRoom() ;
	}
}
