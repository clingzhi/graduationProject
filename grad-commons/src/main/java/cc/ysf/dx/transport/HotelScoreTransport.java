package cc.ysf.dx.transport;

import cc.ysf.dx.pojo.entity.ItripComment;
import cc.ysf.dx.pojo.entity.ItripImage;
import cc.ysf.dx.pojo.entity.LabelDic;
import cc.ysf.dx.pojo.vo.ItripListCommentVO;
import cc.ysf.dx.pojo.vo.ItripScoreCommentVO;
import cc.ysf.dx.pojo.vo.ItripSearchCommentVO;
import cc.ysf.dx.pojo.vo.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * >>> 爱旅行--传输层接口 酒店房间评分
 */
@FeignClient("grad-biz-provider")
@RequestMapping("/comment/trans")
public interface HotelScoreTransport {
	/**
	 * >>> 根据条件查询酒店评分
	 * @param query
	 * @return
	 */
	@PostMapping("/query")
	ItripScoreCommentVO getHotelScore(@RequestBody ItripComment query) throws Exception;

	/**
	 * >>> 根据条件查询酒店各种评论数量
	 * @param param
	 * @return
	 */
	@PostMapping("/getcount")
	Integer getCommentCountByMap(@RequestBody Map<String, Object> param) throws Exception;


	/**
	 * >>> 查询评论内容
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/getcontent")
	Page<ItripListCommentVO> getContent(@RequestBody Map<String,Object> paramMap,@RequestParam Integer pageNo,@RequestParam Integer pageSize)throws Exception;
	/**
	 * >>> 添加评论
	 * @param itripComment
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/add")
	Boolean addComment(@RequestBody ItripComment itripComment)throws Exception;

	/**
	 * >>> 获取出游类型
	 * @param query
	 * @return
	 */
	@PostMapping("/gettraveltype")
	List<LabelDic> getTravelType(@RequestBody LabelDic query)throws Exception;

	/**
	 * ??? 保存图片
	 * @param save
	 * @return
	 */
	@PostMapping("/saveImg")
	boolean saveImg(@RequestBody ItripImage save)throws Exception;

	/**
	 * >>> 根据ID 获取评论对象
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/getcomment")
	ItripComment getComment(@RequestParam Long orderId)throws Exception;
}
