package com.dianping.georpc.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dianping.geo.map.CoordTransferService;
import com.dianping.geo.map.entity.CoordType;
import com.dianping.geo.map.entity.GeoPoint;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:config/spring/common/appcontext-*.xml",
		"classpath*:config/spring/local/appcontext-*.xml" })
public class MapTest {

	@Autowired
	CoordTransferService coordTransferService;

	@Test
	public void testTransferOrNot() {
		/** 不在大陆境内，不进行转换 */
		GeoPoint origin = new GeoPoint(31.22, 2.332);
		GeoPoint offset = coordTransferService.coordTranfer(origin,
				CoordType.MAPBAR, CoordType.GOOGLE);
		assertEquals(offset.getLat(), 31.22, 0.00001);
		assertEquals(offset.getLng(), 2.332, 0.00001);

		/** 大陆境内，进行转换 */
		GeoPoint origin1 = new GeoPoint(31.25536, 121.36645);
		GeoPoint offset1 = coordTransferService.coordTranfer(origin1,
				CoordType.MAPBAR, CoordType.GOOGLE);
		assertEquals(offset1.getLat(), 31.25595, 0.0001);
		assertEquals(offset1.getLng(), 121.36124, 0.0001);
	}

	@Test
	public void testMapbarTo02() {
		/** MapBar转换为02坐标系 */
		GeoPoint origin = new GeoPoint(31.25436, 121.36645);
		GeoPoint offset = coordTransferService.coordTranfer(origin,
				CoordType.MAPBAR, CoordType.GOOGLE);
		assertEquals(offset.getLat(), 31.25502, 0.0001);
		assertEquals(offset.getLng(), 121.36125, 0.0001);

		/** 02转换为MapBar坐标系 */
		GeoPoint origin1 = new GeoPoint(31.25436, 121.36645);
		GeoPoint offset1 = coordTransferService.coordTranfer(origin1,
				CoordType.GOOGLE, CoordType.MAPBAR);
		assertEquals(offset1.getLat(), 31.2537, 0.0001);
		assertEquals(offset1.getLng(), 121.37165, 0.0001);
	}

	@Test
	public void test02To84() {
		/** 02To84 */
		GeoPoint origin = new GeoPoint(31.25436, 121.36645);
		GeoPoint offset = coordTransferService.coordTranfer(origin,
				CoordType.GOOGLE, CoordType.GPS);
		assertEquals(offset.getLat(), 31.256208, 0.0001);
		assertEquals(offset.getLng(), 121.361831, 0.0001);

		/** 84To02 */
		GeoPoint origin1 = new GeoPoint(31.25436, 121.36645);
		GeoPoint offset1 = coordTransferService.coordTranfer(origin1,
				CoordType.GPS, CoordType.GOOGLE);
		assertEquals(offset1.getLat(), 31.252512, 0.0001);
		assertEquals(offset1.getLng(), 121.371069, 0.0001);
	}

	@Test
	public void test84ToBaidu() {
		/** 84ToBaidu */
		GeoPoint origin1 = new GeoPoint(31.25436, 121.36645);
		GeoPoint offset1 = coordTransferService.coordTranfer(origin1,
				CoordType.GPS, CoordType.BAIDU);
		assertEquals(offset1.getLat(), 31.258172, 0.0001);
		assertEquals(offset1.getLng(), 121.377685, 0.0001);

		/** BaiduTo84 */
		GeoPoint cd02ToMapbar = new GeoPoint(31.25436, 121.36645);
		GeoPoint result02ToMapbar = coordTransferService.coordTranfer(
				cd02ToMapbar, CoordType.BAIDU, CoordType.GPS);
		assertEquals(result02ToMapbar.getLat(), 31.250548, 0.0001);
		assertEquals(result02ToMapbar.getLng(), 121.355215, 0.0001);
	}

}
