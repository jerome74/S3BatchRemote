package com.enel.s3mock.util;

import com.enel.s3mock.model.Entity;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Flow;

public interface IEntitiesUtil {

    final static List<Entity> ENTITIES = List.of(Entity.builder().name("bay").msName("mebaynetwbtch").endpoint("mebaynetwbtch.glin-ap31312mp01160-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractBay")
            .msNumber("01160").build(), Entity.builder().name("busbar").msName("mebusbarnetwbtch").endpoint("mebusbarnetwbtch.glin-ap31312mp01161-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractBusbar")
            .msNumber("01161").build(), Entity.builder().name("connection").msName("meconnectionnetwbtch").endpoint("meconnectionnetwbtch.glin-ap31312mp01176-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractConnection")
            .msNumber("01176").build(), Entity.builder().name("manhole").msName("meinframanholebtch").endpoint("meinframanholebtch.glin-ap31312mp02075-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractManhole")
            .msNumber("02075").build(), Entity.builder().name("compensator").msName("mecompensnetwbtch").endpoint("mecompensnetwbtch.glin-ap31312mp01163-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractCompensator")
            .msNumber("01163").build(), Entity.builder().name("equipment").msName("meequipmentnetwbtch").endpoint("meequipmentnetwbtch.glin-ap31312mp01166-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractEquipment")
            .msNumber("01166").build(), Entity.builder().name("grounding").msName("megroundingnetwbtch").endpoint("megroundingnetwbtch.glin-ap31312mp001167-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractGrounding")
            .msNumber("01167").build(), Entity.builder().name("line").msName("melinenetwbtch ").endpoint("melinenetwbtch.glin-ap31312mp01168-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractLine")
            .msNumber("01168").build(), Entity.builder().name("node").msName("menodenetwbtch").endpoint("menodenetwbtch.glin-ap31312mp01164-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractNode")
            .msNumber("01164").build(), Entity.builder().name("esegment").msName("mesegmentnetwbtch").endpoint("mesegmentnetwbtch.glin-ap31312mp01169-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractSegment")
            .msNumber("01169").build(), Entity.builder().name("station").msName("mestationnetwbtch").endpoint("mestationnetwbtch.glin-ap31312mp01170-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractStation")
            .msNumber("01170").build(), Entity.builder().name("switch").msName("meswitchnetwbtch").endpoint("meswitchnetwbtch.glin-ap31312mp01171-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractSwitch")
            .msNumber("01171").build(), Entity.builder().name("system").msName("mesystemnetwbtch").endpoint("mesystemnetwbtch.glin-ap31312mp01162-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractSystem")
            .msNumber("01162").build(), Entity.builder().name("terminal").msName("meterminalnetwbtch").endpoint("meterminalnetwbtch.glin-ap31312mp01172-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractTerminal")
            .msNumber("01172").build(), Entity.builder().name("transformer").msName("metransfnetwbtch").endpoint("metransfnetwbtch.glin-ap31312mp01173-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractTransformer")
            .msNumber("01173").build(), Entity.builder().name("fastening").msName("meinfrafasteningbtch").endpoint("meinfrafasteningbtch.glin-ap31312mp02083-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractFastening")
            .msNumber("02083").build(), Entity.builder().name("winding").msName("mewindingnetwbtch").endpoint("mewindingnetwbtch.glin-ap31312mp01174-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractWinding")
            .msNumber("01174").build(), Entity.builder().name("support").msName("meinfrasupportbtch").endpoint("meinfrasupportbtch.glin-ap31312mp02080-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractSupport")
            .msNumber("02080").build(), Entity.builder().name("other").msName("meinfraotherbtch").endpoint("meinfraotherbtch.glin-ap31312mp02082-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractOtherInfrastructure")
            .msNumber("02082").build(), Entity.builder().name("extractline").msName("menetworkextrbatch").endpoint("menetworkextrbatch.glin-ap31312mp02018-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractLineExtract")
            .msNumber("02018").build(), Entity.builder().name("windinghst").msName("mewindinghstnetwbtch").endpoint("mewindinghstnetwbtch.glin-ap31312mp02580-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractWindingHST")
            .msNumber("02580").build(), Entity.builder().name("stationhst").msName("mestationhstnetwbtch").endpoint("mestationhstnetwbtch.glin-ap31312mp02555-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractStationHST")
            .msNumber("02555").build(), Entity.builder().name("linehst").msName("melinehstnetwbtch").endpoint("melinehstnetwbtch.glin-ap31312mp02557-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractLineHST")
            .msNumber("02557").build(), Entity.builder().name("connectionhst").msName("meconnecthstnetwbtch").endpoint("meconnecthstnetwbtch.glin-ap31312mp02581-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractConnectionHST")
            .msNumber("02581").build(), Entity.builder().name("transformerhst").msName("metransfhstnetwbtch").endpoint("metransfhstnetwbtch.glin-ap31312mp02558-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractTransformerHST")
            .msNumber("02558").build(), Entity.builder().name("switchhst").msName("meswitchhstnetwbtch").endpoint("meswitchhstnetwbtch.glin-ap31312mp02577-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractSwitchHST")
            .msNumber("02577").build(), Entity.builder().name("groundinghst").msName("megroundhstnetwbtch").endpoint("megroundhstnetwbtch.glin-ap31312mp02559-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractGroundingHST")
            .msNumber("02559").build(), Entity.builder().name("segmenthst").msName("mesegmenthstnetwbtch").endpoint("mesegmenthstnetwbtch.glin-ap31312mp02554-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractSegmentHST")
            .msNumber("02554").build(), Entity.builder().name("busbarhst").msName("mebusbarhstnetwbtch").endpoint("mebusbarhstnetwbtch.glin-ap31312mp02576-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractBusbarHST")
            .msNumber("02576").build(), Entity.builder().name("bayhst").msName("mebayhstnetwbtch").endpoint("mebayhstnetwbtch.glin-ap31312mp02556-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractBayHST")
            .msNumber("02556").build(), Entity.builder().name("componentbond").msName("menetwcompbtch").endpoint("menetwcompbtch.glin-ap31312mp02397-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractComponentBond")
            .msNumber("02397").build(), Entity.builder().name("feature").msName("mecompfeaturesbtch").endpoint("mecompfeaturesbtch.glin-ap31312mp02400-[DEV]-platform-namespace").version("v1").extractor("ap31312bp01058").sparkEntity("OfficialExtractComponentFeature")
            .msNumber("02400").build());

}
