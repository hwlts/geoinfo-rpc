package com.dianping.georpc.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dianping.geo.location.MobiLocateService;
import com.dianping.geo.location.MobiOffsetService;
import com.dianping.geo.location.MobiRGCService;
import com.dianping.geo.location.entity.Address;
import com.dianping.geo.location.entity.Coord;
import com.dianping.geo.location.entity.CoordSource;
import com.dianping.geo.map.GeocodingService;
import com.dianping.geo.map.entity.CoordType;
import com.dianping.geo.map.entity.GeoPoint;

/**
 * 
 * @author zhufeng.liu
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:config/spring/common/appcontext-*.xml",
		"classpath*:config/spring/local/appcontext-*.xml" })
public class LocationRPCTest {

	@Autowired
	private MobiLocateService mobiLocateService;

	@Autowired
	private MobiRGCService mobiRGCService;

	@Autowired
	private MobiOffsetService mobiOffsetService;

	@Autowired
	private GeocodingService geocodingService;

	@Test
	public void rgcTest() {
		GeoPoint gp = new GeoPoint(31.1514, 121.45558);
		String addr = geocodingService.revGeocoding(gp, CoordType.GPS);
		System.out.println(addr);
	}

	@Test
	public void geocodingTest() {
		String addr = "中国上海市浦东新区沿浦路漕河泾街道陆家宅";
		GeoPoint gp = geocodingService.geocoding(1, addr);
		System.out.println(gp);
	}

	@Test
	public void mobiRGCTest() {
		Coord coordInDB = new Coord(31.187966, 121.436951, 500, 0,
				CoordType.GPS, CoordSource.GPS);
		Coord coordFromGoogle = new Coord(30.887789, 120.084, 500, 0,
				CoordType.GPS, CoordSource.GPS);
		Address addrInDB = mobiRGCService.getAddress(coordInDB);
		Address addrFromGoogle = mobiRGCService.getAddress(coordFromGoogle);
		try {
			System.out.println("address form database:" + addrInDB);
			System.out.println("address from google: " + addrFromGoogle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void locateTest() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("did", "863994013890629");
		params.put("client", "Android_api");
		params.put("version", "5.5.1");
		params.put("uid", "1599639");
		params.put("ua",
				"MApi 1.0 (com.dianping.v1 5.5.1 dianping-m ZTE_U930; Android 4.0.3)");
		params.put("session", "f18f9992-5420-42dd-a2d8-2942a87ec9e4");
		params.put("impl", "13");
		params.put("action", "loc");
		params.put("elapse", "0");
		params.put("debug", "0");
		params.put("gsm", "460,0:62561,6291,0");
		params.put("cdma", "");
		params.put(
				"wifi",
				"123456789,94:0c:6d:7a:1d:52,-54|TP-BENK,ec:88:8f:92:9a:e8,-54|CU_7hWX,70:b9:21:13:2d:c0,-52|kaikai0416,00:b0:0c:1a:26:f0,-75|wangluoshi,6c:e8:73:37:4e:f2,-78|xinling,0a:1b:b1:22:32:27,-79|hpedu,06:1b:b1:22:32:27,-78|ChinaNet-vX33,0e:4c:39:75:f3:ec,-83|jackie,6c:e8:73:7c:6a:b0,-92|ChinaNet-qDAm,72:a5:1b:7a:f8:58,-86|lijun,14:d6:4d:72:00:06,-95|TP-LINK_A45182,ec:88:8f:a4:51:82,-84|yaoweiYY,b8:a3:86:8d:36:37,-95|xinling,0a:1b:b1:22:32:a2,-96|9H_WaiWang,58:66:ba:e8:e0:82,-86|ChinaNet,58:66:ba:e8:e1:80,-86|9H_NeiWang,58:66:ba:e8:e1:81,-92|9H_WaiWang,58:66:ba:e8:e1:82,-88|9H_WaiWang,58:66:ba:e8:df:a2,-91|9H_NeiWang,58:66:ba:e8:df:a1,-92|ChinaNet,58:66:ba:e8:df:a0,-92|xqr61xqr61,00:21:27:7c:e9:58,-94|9H_WaiWang,58:66:ba:e8:de:52,-94|9H_NeiWang,58:66:ba:e8:de:51,-95|ChinaNet,58:66:ba:ea:85:10,-98|9H_WaiWang,58:66:ba:ea:85:12,-101");
		params.put("coord_cell", "");
		params.put("coord_wifi", "");
		params.put("client", "Android_api");
		params.put("coord_acell", "");
		params.put("coord_awifi", "31.205959,121.48148900000001@369,605");
		params.put("coord_bcell", "");
		params.put("coord_bwifi", "31.205339,121.48251900000001@69,466");
		params.put("coord_network", "");
		params.put("coord_gps", "");

		List<Coord> coords = mobiLocateService.locate(params);
		if (coords != null && coords.size() > 0) {
			for (Coord coord : coords) {
				try {
					JSONObject jo = new JSONObject(coord.toString());
					System.out.println(jo.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	public void offsetTest() {
		Coord srcCoord = new Coord(31.1514, 121.45558, 500, 0, CoordType.BAIDU,
				CoordSource.BCELL);
		Coord dstCoord = mobiOffsetService.offset(srcCoord, CoordType.GOOGLE);
		try {
			JSONObject jo = new JSONObject(dstCoord.toString());
			System.out.println(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
