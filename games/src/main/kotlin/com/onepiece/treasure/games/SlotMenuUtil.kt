package com.onepiece.treasure.games

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import java.io.File

object SlotMenuUtil {


    val jokerJson = "{\"New\":[\"79mafnrjt48aa\",\"k3anse3yrrunq\",\"pirtanombyroh\",\"ne4gq55cpitgg\",\"kia1eetdryo1c\",\"u17q53q45xcp1\",\"ef1uyxt98o6ur\",\"9xpa7brfxj7zo\",\"naagsa5ycfugq\",\"i4rc816e388c6\",\"nh9swadbc3use\",\"9upe5bm4xph81\",\"tqi9778i7mi6o\",\"9mqe9bhroi78s\",\"byz81hmsq748k\",\"113qm5xnhxoqn\",\"5m6k9j7rwspjs\",\"igg7tisz4ukhw\",\"w4ypzw6o48mpq\",\"xbxy1yegyhnyk\",\"foff4ikkjprr1\",\"ruufkzk1kpefn\",\"awn5jciusna5c\",\"3hj4fkfji4z4a\",\"4akkze7ywgukq\",\"86burqb38a9ua\",\"o7f9ih8t6559e\",\"8u9r4tj48chd1\",\"wtupmzq14xepn\",\"d8cso3u8ct1iw\",\"3yfmucpss64mk\",\"bmr8675wqiigs\",\"bzgza4umpbwsh\",\"s77hiogba5dhe\",\"dxxsh3dfmjpio\",\"7cz37fritkfao\",\"7tccifcktqre1\",\"zygj7oqga9nck\",\"srd3xusx3ughr\",\"1ru5x5zx7us6r\",\"dkzdo35rcipfs\",\"q9gi4yybyadoe\",\"wykepsq659qp4\",\"rsjogw1ukbeic\",\"5ii9zgw5unc3h\",\"xmzfobaryz7xs\",\"qxoindypyeboy\",\"aij68ciusna5c\",\"satj3o6ya8dcq\",\"8nsbhokge7nrk\",\"ww3a8wsu4de7c\",\"43bx3e7ywgukq\",\"ioheiiqk3xrc1\",\"7f9h9fwz11kaw\",\"84igeq3a8r9d6\",\"fk9yoi4wkifrs\",\"ateqfxp1sqamn\",\"ie9eti6w4zfcs\",\"oajk3h9o685xq\",\"ur8593z8hu17w\",\"j9nzkkbjfaz1a\",\"4tyxfmpnwqokn\",\"soojfuqnaxycn\",\"9ii7s6u5xbhzh\",\"wcaadzg74mj7y\",\"xkhy6baryz7xs\",\"qq5ocdypyeboy\",\"g54rso4yefdrq\",\"ary5bxi9z165r\",\"b8rzo7uzqt4sw\",\"8d7r1okge7nrk\",\"nzkseaudcbosc\",\"wi17jwsu4de7c\",\"1jeqx59c7ztqg\",\"4omkmmpnwqokn\",\"st5cmuqnaxycn\",\"ddpg1amgc71gk\",\"p63ornyjba8oa\",\"xq9ohbyf9m79o\",\"kk8nqm3cfwtng\"],\"Hot\":[\"fwria11mjbrwh\",\"8rqwot18etnuw\",\"dhdirsn3m3xia\",\"79mafnrjt48aa\",\"k3anse3yrrunq\",\"pirtanombyroh\",\"ne4gq55cpitgg\",\"kia1eetdryo1c\",\"u17q53q45xcp1\",\"ef1uyxt98o6ur\",\"9xpa7brfxj7zo\",\"naagsa5ycfugq\",\"i4rc816e388c6\",\"nh9swadbc3use\",\"9upe5bm4xph81\",\"tqi9778i7mi6o\",\"9mqe9bhroi78s\",\"byz81hmsq748k\",\"113qm5xnhxoqn\",\"5m6k9j7rwspjs\",\"igg7tisz4ukhw\",\"w4ypzw6o48mpq\",\"xbxy1yegyhnyk\",\"ywozehuuqbazc\",\"foff4ikkjprr1\",\"bcizh7dipjiso\",\"55hj8ghaugxj6\",\"jpiuhpbifei1o\",\"ruufkzk1kpefn\",\"awn5jciusna5c\",\"3hj4fkfji4z4a\",\"4akkze7ywgukq\",\"86burqb38a9ua\",\"o7f9ih8t6559e\",\"8u9r4tj48chd1\",\"wtupmzq14xepn\",\"d8cso3u8ct1iw\",\"3yfmucpss64mk\",\"itzp5iqk3xrc1\",\"7rw3tfwz11kaw\",\"aodmmxp1sqamn\",\"o3nxzh9o685xq\",\"ufc6t3z8hu17w\",\"j6j1rkbjfaz1a\",\"9w6aa6u5xbhzh\",\"wpu7pzg74mj7y\",\"tbfxuhxs694xk\",\"gd3rn1kqj7gr4\",\"wfo7bzs95uq7r\",\"jsguaktmfyw1h\",\"bmr8675wqiigs\",\"bzgza4umpbwsh\",\"s77hiogba5dhe\",\"dxxsh3dfmjpio\",\"7cz37fritkfao\",\"7tccifcktqre1\",\"zygj7oqga9nck\",\"srd3xusx3ughr\",\"1ru5x5zx7us6r\",\"dkzdo35rcipfs\",\"q9gi4yybyadoe\",\"wykepsq659qp4\",\"rsjogw1ukbeic\",\"5ii9zgw5unc3h\",\"xmzfobaryz7xs\",\"qxoindypyeboy\",\"aij68ciusna5c\",\"satj3o6ya8dcq\",\"8nsbhokge7nrk\",\"ww3a8wsu4de7c\",\"43bx3e7ywgukq\",\"ioheiiqk3xrc1\",\"7f9h9fwz11kaw\",\"84igeq3a8r9d6\",\"fk9yoi4wkifrs\",\"ateqfxp1sqamn\",\"ie9eti6w4zfcs\",\"oajk3h9o685xq\",\"ur8593z8hu17w\",\"j9nzkkbjfaz1a\",\"4tyxfmpnwqokn\",\"soojfuqnaxycn\",\"9ii7s6u5xbhzh\",\"wcaadzg74mj7y\",\"xkhy6baryz7xs\",\"qq5ocdypyeboy\",\"g54rso4yefdrq\",\"ary5bxi9z165r\",\"b8rzo7uzqt4sw\",\"8d7r1okge7nrk\",\"nzkseaudcbosc\",\"wi17jwsu4de7c\",\"1jeqx59c7ztqg\",\"4omkmmpnwqokn\",\"st5cmuqnaxycn\",\"ddpg1amgc71gk\",\"p63ornyjba8oa\",\"xq9ohbyf9m79o\",\"kk8nqm3cfwtng\",\"dc7sh3dfmjpio\",\"3uim5ppkiqwk1\",\"j3wngk3efrzn6\",\"yr1zy9u9xt6zr\",\"hxb3p8r71kj3y\"],\"Slot\":[\"fwria11mjbrwh\",\"8rqwot18etnuw\",\"kf41ymtxfos1r\",\"ebudnqj68h6d4\",\"xtpy4bx49xhx1\",\"1q36p58phmt6y\",\"dhdirsn3m3xia\",\"rh8iwwntk3mie\",\"axt5pxf7sk35y\",\"69xaiyrbo4dae\",\"oqt9p9876m39y\",\"jbzd1cjsgh4dk\",\"4d5kdkpqi6sk4\",\"u6d7fsg355x7a\",\"t656f48j75z6a\",\"z1pc5tp4zqhm1\",\"bes8675wqiigs\",\"bwwza4umpbwsh\",\"s6xhiogba5dhe\",\"5864tji8w113w\",\"qieoeyodyyyoc\",\"79mafnrjt48aa\",\"k3anse3yrrunq\",\"pirtanombyroh\",\"ne4gq55cpitgg\",\"kia1eetdryo1c\",\"u17q53q45xcp1\",\"ef1uyxt98o6ur\",\"9xpa7brfxj7zo\",\"naagsa5ycfugq\",\"i4rc816e388c6\",\"nh9swadbc3use\",\"9upe5bm4xph81\",\"tqi9778i7mi6o\",\"9mqe9bhroi78s\",\"byz81hmsq748k\",\"113qm5xnhxoqn\",\"5m6k9j7rwspjs\",\"igg7tisz4ukhw\",\"w4ypzw6o48mpq\",\"xbxy1yegyhnyk\",\"ywozehuuqbazc\",\"foff4ikkjprr1\",\"bcizh7dipjiso\",\"55hj8ghaugxj6\",\"jpiuhpbifei1o\",\"ruufkzk1kpefn\",\"awn5jciusna5c\",\"3hj4fkfji4z4a\",\"4akkze7ywgukq\",\"86burqb38a9ua\",\"o7f9ih8t6559e\",\"8u9r4tj48chd1\",\"wtupmzq14xepn\",\"d8cso3u8ct1iw\",\"3yfmucpss64mk\",\"itzp5iqk3xrc1\",\"7rw3tfwz11kaw\",\"aodmmxp1sqamn\",\"o3nxzh9o685xq\",\"ufc6t3z8hu17w\",\"j6j1rkbjfaz1a\",\"9w6aa6u5xbhzh\",\"wpu7pzg74mj7y\",\"tbfxuhxs694xk\",\"gd3rn1kqj7gr4\",\"wfo7bzs95uq7r\",\"jsguaktmfyw1h\",\"bmr8675wqiigs\",\"bzgza4umpbwsh\",\"s77hiogba5dhe\",\"dxxsh3dfmjpio\",\"7cz37fritkfao\",\"7tccifcktqre1\",\"zygj7oqga9nck\",\"srd3xusx3ughr\",\"1ru5x5zx7us6r\",\"dkzdo35rcipfs\",\"q9gi4yybyadoe\",\"wykepsq659qp4\",\"rsjogw1ukbeic\",\"5ii9zgw5unc3h\",\"xmzfobaryz7xs\",\"qxoindypyeboy\",\"aij68ciusna5c\",\"satj3o6ya8dcq\",\"8nsbhokge7nrk\",\"ww3a8wsu4de7c\",\"43bx3e7ywgukq\",\"ioheiiqk3xrc1\",\"7f9h9fwz11kaw\",\"84igeq3a8r9d6\",\"fk9yoi4wkifrs\",\"ateqfxp1sqamn\",\"ie9eti6w4zfcs\",\"oajk3h9o685xq\",\"ur8593z8hu17w\",\"j9nzkkbjfaz1a\",\"4tyxfmpnwqokn\",\"soojfuqnaxycn\",\"9ii7s6u5xbhzh\",\"wcaadzg74mj7y\"],\"Fishing\":[\"xkhy6baryz7xs\",\"qq5ocdypyeboy\",\"g54rso4yefdrq\",\"ary5bxi9z165r\",\"b8rzo7uzqt4sw\",\"8d7r1okge7nrk\",\"nzkseaudcbosc\",\"wi17jwsu4de7c\",\"1jeqx59c7ztqg\",\"4omkmmpnwqokn\",\"st5cmuqnaxycn\",\"ddpg1amgc71gk\",\"p63ornyjba8oa\",\"xq9ohbyf9m79o\",\"kk8nqm3cfwtng\"],\"Table\":[\"dc7sh3dfmjpio\",\"3uim5ppkiqwk1\",\"j3wngk3efrzn6\",\"yr1zy9u9xt6zr\",\"hxb3p8r71kj3y\"]}"
    val microJson = "{\"Hot\":[\"1229_1002\",\"1159_1002\",\"1103_1002\",\"1283_1002\",\"1117_1002\",\"1287_1002\",\"1207_1002\",\"1133_1002\",\"1389_1002\",\"1023_1002\",\"1882_1002\",\"1318_1002\",\"1035_1002\",\"1002_1002\",\"1037_1002\",\"1171_1002\"],\"New\":[\"2069_1002\",\"2070_1002\",\"2067_1002\",\"2066_1002\",\"2068_1002\",\"2065_1002\",\"2064_1002\",\"2061_1002\",\"2060_1002\",\"1286_1002\",\"2047_1002\",\"1996_1002\",\"1994_1002\",\"1995_1002\",\"1992_1002\"],\"Slot\":[\"1229_1002\",\"1159_1002\",\"1103_1002\",\"1283_1002\",\"1117_1002\",\"1287_1002\",\"1207_1002\",\"1133_1002\",\"1389_1002\",\"1097_1002\",\"1882_1002\",\"1318_1002\",\"1035_1002\",\"1002_1002\",\"1037_1002\",\"1171_1002\",\"1047_1002\",\"1163_1002\",\"1321_1002\",\"1261_1001\",\"1103_1002\",\"1250_1002\",\"1151_1002\",\"1389_1002\",\"1049_1002\",\"1245_1002\",\"1126_1002\",\"1060_1002\",\"1060_1002\",\"1314_1002\",\"1343_1002\",\"1308_1002\",\"1308_1002\",\"1254_1002\",\"1254_1002\",\"1302_1002\",\"1035_1002\",\"1010_1002\",\"1246_1002\",\"1155_1002\",\"1004_1002\",\"1004_1002\",\"1147_1002\",\"1021_1002\",\"1384_1002\",\"1260_1001\",\"1013_1002\",\"1013_1002\",\"1188_1002\",\"1045_1002\",\"1312_1002\",\"4111_1002\",\"4296_1002\",\"1159_1002\",\"4297_1002\",\"4257_1002\",\"1207_1002\",\"4110_1002\",\"1133_1002\",\"1294_1002\",\"4295_1002\",\"1290_1002\",\"4288_1002\",\"4289_1002\",\"1275_1002\",\"1293_1002\",\"4109_1002\",\"4287_1002\",\"1229_1002\",\"4112_1002\",\"1023_1002\",\"1100_1002\",\"4290_1002\",\"1189_1002\",\"4286_1002\",\"1097_1002\",\"4271_1002\",\"1360_1002\",\"1009_1002\",\"1318_1002\",\"1173_1002\",\"1173_1002\",\"1095_1002\",\"1117_1002\",\"1381_1001\",\"1300_1001\",\"1366_1002\",\"1366_1002\",\"1274_1002\",\"1291_1002\",\"1001_1002\",\"1374_1002\",\"1032_1002\",\"1002_1002\",\"1404_1002\",\"1327_1002\",\"1167_1002\",\"1167_1002\",\"1320_1002\",\"1130_1002\",\"1234_1002\",\"1234_1002\",\"1395_1002\",\"1200_1002\",\"1200_1002\",\"1169_1002\",\"1386_1002\",\"1028_1002\",\"1330_1002\",\"1236_1002\",\"1330_1002\",\"1028_1002\",\"1208_1002\",\"1385_1002\",\"1292_1001\",\"1122_1002\",\"1122_1002\",\"1020_1002\",\"1186_1002\",\"1222_1002\",\"1222_1002\",\"1171_1002\",\"1345_1002\",\"1041_1002\",\"2069_1002\",\"2070_1002\",\"2067_1002\",\"2066_1002\",\"2068_1002\",\"2065_1002\",\"2064_1002\",\"1885_1002\",\"2061_1002\",\"2060_1002\",\"1286_1002\",\"2047_1002\",\"1996_1002\",\"1994_1002\",\"1992_1002\"],\"Table\":[\"1248_1001\",\"1042_1001\",\"1329_1001\",\"1303_1001\",\"1249_1001\",\"1265_1001\",\"1203_1001\",\"1090_1001\",\"1078_1001\",\"1369_1001\",\"1233_1001\",\"1213_1001\",\"1217_1001\",\"1105_1001\",\"1415_1002\",\"1416_1001\",\"1418_1001\",\"1059_1001\"],\"Arcrade\":[\"1088_1001\",\"1259_1001\",\"1335_1001\",\"1096_1001\",\"4298_1002\",\"1995_1002\"],\"Scratch\":[\"1076_1001\",\"1344_1001\",\"1402_1001\",\"1003_1001\",\"1174_1001\",\"1390_1001\",\"1119_1001\",\"1015_1001\",\"1089_1001\",\"1044_1001\",\"1112_1001\",\"1182_1001\",\"1086_1001\",\"1224_1001\",\"1353_1001\",\"1355_1001\",\"1226_1001\",\"1990_1001\",\"1196_1001\",\"1231_1001\",\"1336_1001\",\"1029_1001\",\"1161_1001\",\"1403_1001\",\"1301_1001\"]}"
    val pragmaticWapJson = "{\"Hot\":[\"vs1fortunetree\",\"vs10firestrike\",\"vs243lionsgold\",\"vs243fortune\",\"vs243caishien\",\"vs25journey\",\"vs25wolfgold\",\"vs20rhino\",\"vs25mustang\",\"scsafariai\",\"vs20honey\",\"vs20chicken\",\"vs1dragon8\",\"vs9madmonkey\",\"vs18mashang\",\"vs5aztecgems\"],\"New\":[\"vs5spjoker\",\"vs25scarabqueen\",\"vs243lionsgold\",\"vs1fortunetree\",\"vs20chicken\",\"vs10vampwolf\",\"vs9hotroll\",\"vs243mwarrior\",\"bndt\",\"vs5trjokers\",\"vs20wildpix\",\"vs20fruitsw\",\"vs243caishien\",\"vs40pirate\",\"vs20doghouse\"],\"Keno\":[\"kna\"],\"Slot\":[\"vs1fortunetree\",\"vs10firestrike\",\"vs243lionsgold\",\"vs243fortune\",\"vs243caishien\",\"vs25journey\",\"vs25wolfgold\",\"vs20rhino\",\"vs25mustang\",\"vs25safari\",\"vs20honey\",\"vs20chicken\",\"vs1dragon8\",\"vs9madmonkey\",\"vs18mashang\",\"vs5aztecgems\",\"vs20sbxmas\",\"vs20hercpeg\",\"vs1tigers\",\"cs5moneyroll\",\"cs3irishcharms\",\"cs5triple8gold\",\"cs3w\",\"vs20egypttrs\",\"vs10fruity2\",\"vs25gladiator\",\"vs25goldpig\",\"vs50safariking\",\"vs20leprexmas\",\"vs5trdragons\",\"vs10egyptcls\",\"vs20vegasmagic\",\"vs9chen\",\"vs25davinci\",\"vs25peking\",\"vs20leprechaun\",\"vs1024butterfly\",\"vs10madame\",\"vs25asgard\",\"vs243lions\",\"vs25champ\",\"vs5joker\",\"vs15fairytale\",\"vs7fire88\",\"vs25chilli\",\"vs10egypt\",\"vs25newyear\",\"vs25goldrush\",\"vs20santa\",\"vs25pandagold\",\"vs7pigs\",\"vs15diamond\",\"vs25vegas\",\"vs25wildspells\",\"vs50pixie\",\"vs3train\",\"vs4096jurassic\",\"vs20eightdragons\",\"vs25kingdoms\",\"vs25wolfgold\",\"vs25pantherqueen\",\"vs1024atlantis\",\"vs25queenofgold\",\"vs25dragonkingdom\",\"vs50hercules\",\"vs50aladdin\",\"vs30catz\",\"vs40beowulf\",\"vs50chinesecharms\",\"vs9hockey\",\"vs25dwarves_new\",\"vs25romeoandjuliet\",\"vs9catz\",\"vs50kingkong\",\"vs20godiva\",\"vs15ktv\",\"vs243crystalcave\",\"vs20hockey\",\"vs50amt\",\"vs13ladyofmoon\",\"vs20egypt\",\"vs20cm\",\"vs20cw\",\"vs20cms\",\"vs20cmv\",\"vs20gg\",\"vs25sea\",\"vs20rome\",\"vs25h\",\"vs25dwarves\",\"vs15b\",\"vs13g\",\"vs20bl\",\"vs7monkeys\",\"sc7piggiesai\",\"scdiamondai\",\"scgoldrushai\",\"scpandai\",\"scqogai\",\"scsafariai\",\"scwolfgold\",\"vs5spjoker\",\"vs25scarabqueen\",\"vs10vampwolf\",\"vs9hotroll\",\"vs243mwarrior\",\"vs5trjokers\",\"vs20wildpix\",\"vs20fruitsw\",\"vs40pirate\",\"vs20doghouse\"],\"Table\":[\"bnadvanced\",\"bca\",\"bjmb\",\"bjma\",\"rla\",\"vpa\",\"bndt\"]}"
    val pramaticWebJson = "{\"Hot\":[\"vs1fortunetree\",\"vs243lionsgold\",\"cs5triple8gold\",\"vs1dragon8\",\"vs243caishien\",\"vs9madmonkey\",\"vs25wolfgold\",\"vs243fortune\",\"vs9hotroll\",\"vs20rhino\",\"vs7776secrets\",\"vs25goldpig\",\"vs25journey\",\"vs25chilli\",\"vs243lionsgold\",\"vs243mwarrior\",\"vs5joker\"],\"New\":[\"vs5spjoker\",\"vs25scarabqueen\",\"vs1fortunetree\",\"vs20chicken\",\"vs10vampwolf\",\"vs9hotroll\",\"vs7776secrets\",\"vs243mwarrior\",\"bndt\",\"vs5trjokers\",\"vs243lionsgold\",\"vs20wildpix\",\"vs20fruitsw\",\"vs243caishien\",\"vs40pirate\",\"vs20doghouse\"],\"Slot\":[\"vs1fortunetree\",\"vs243lionsgold\",\"vs1dragon8\",\"vs243caishien\",\"vs9madmonkey\",\"vs25wolfgold\",\"vs243fortune\",\"vs9hotroll\",\"vs20rhino\",\"vs5aztecgems\",\"vs25goldpig\",\"vs25journey\",\"vs25chilli\",\"vs243lions\",\"vs243mwarrior\",\"vs5joker\",\"vs20sbxmas\",\"vs20hercpeg\",\"vs10firestrike\",\"vs20honey\",\"vs1tigers\",\"cs5moneyroll\",\"cs3irishcharms\",\"cs5triple8gold\",\"cs3w\",\"vs20egypttrs\",\"vs10fruity2\",\"vs25gladiator\",\"vs18mashang\",\"vs50safariking\",\"vs20leprexmas\",\"vs25mustang\",\"vs5trdragons\",\"vs10egyptcls\",\"vs20vegasmagic\",\"vs9chen\",\"vs25davinci\",\"vs25peking\",\"vs20leprexmas\",\"vs1024butterfly\",\"vs10madame\",\"vs25asgard\",\"vs25champ\",\"vs15fairytale\",\"vs7fire88\",\"vs10egypt\",\"vs25newyear\",\"vs25goldrush\",\"vs20santa\",\"vs25pandagold\",\"vs7pigs\",\"vs15diamond\",\"vs25vegas\",\"vs25wildspells\",\"vs50pixie\",\"vs3train\",\"vs4096jurassic\",\"vs20eightdragons\",\"vs25kingdoms\",\"vs25wolfgold\",\"vs25pantherqueen\",\"vs1024atlantis\",\"vs25queenofgold\",\"vs25dragonkingdom\",\"vs50hercules\",\"vs50aladdin\",\"vs30catz\",\"vs40beowulf\",\"vs50chinesecharms\",\"vs9hockey\",\"vs25dwarves_new\",\"vs25romeoandjuliet\",\"vs25safari\",\"vs9catz\",\"vs50kingkong\",\"vs20godiva\",\"vs15ktv\",\"vs243crystalcave\",\"vs20hockey\",\"vs50amt\",\"vs13ladyofmoon\",\"vs20egypt\",\"vs20cm\",\"vs20cw\",\"vs20cmv\",\"vs20cms\",\"vs20gg\",\"vs25sea\",\"vs20rome\",\"vs25h\",\"vs25dwarves\",\"vs13g\",\"vs15b\",\"vs20bl\",\"vs7monkeys\",\"sc7piggiesai\",\"scdiamondai\",\"scgoldrushai\",\"scpandai\",\"scqogai\",\"scsafariai\",\"scwolfgold\",\"vs5spjoker\",\"vs25scarabqueen\",\"vs20chicken\",\"vs10vampwolf\",\"vs7776secrets\",\"vs5trjokers\",\"vs20wildpix\",\"vs20fruitsw\",\"vs40pirate\",\"vs20doghouse\"],\"Keno\":[\"kna\"],\"Table\":[\"bnadvanced\",\"bca\",\"bjmb\",\"bjma\",\"rla\",\"vpa\",\"bndt\"]}"
    val spadeJson = "{\"Hot\":[\"S-GK01\",\"S-PW02\",\"S-FM02\",\"S-LK01\",\"S-LC01\",\"S-ZE01\",\"S-GP01\",\"S-GL02\",\"S-DX01\",\"S-FD01\",\"S-DF02\",\"S-FL02\",\"S-WP02\",\"S-DF01\",\"S-LY01\",\"S-TZ01\"],\"\":[\"\",\"\"],\"New\":[\"S-LS02\",\"S-HE01\",\"S-SB01\",\"S-DF02\",\"S-ML01\",\"S-TP02\",\"S-GP01\",\"S-CH01\",\"S-GK01\",\"S-PG01\",\"S-CP01\",\"S-GF01\",\"S-LY02\",\"S-GA01\",\"S-PW02\",\"S-WP02\"],\"Slot\":[\"S-GK01\",\"S-PW02\",\"S-FM02\",\"S-LK01\",\"S-CS01\",\"S-ZE01\",\"S-GP01\",\"S-GL02\",\"S-DX01\",\"S-FD01\",\"S-DF02\",\"S-FL02\",\"S-WP02\",\"S-DF01\",\"S-LY01\",\"S-TZ01\",\"S-HY01\",\"S-FO01\",\"S-GC03\",\"S-BC01\",\"S-CY01\",\"S-PP01\",\"S-HF01\",\"S-FC03\",\"S-PO01\",\"S-TW01\",\"S-PK01\",\"S-GW01\",\"S-GP02\",\"S-NT01\",\"S-SH01\",\"S-LC01\",\"S-DM01\",\"S-HH01\",\"S-BB01\",\"S-CM01\",\"S-AL01\",\"S-DV01\",\"S-FG01\",\"S-EG03\",\"S-SG02\",\"S-JF02\",\"S-DG04\",\"S-WM03\",\"S-GS04\",\"S-WC03\",\"S-LH03\",\"S-SP03\",\"S-SG04\",\"S-LE03\",\"S-IL03\",\"S-IM03\",\"S-LF01\",\"S-PH02\",\"S-MR01\",\"S-WP01\",\"S-JT01\",\"S-GO01\",\"S-SM01\",\"S-IL02\",\"S-LE02\",\"S-GG01\",\"S-LM01\",\"S-AT02\",\"S-DG03\",\"S-EG02\",\"S-FC02\",\"S-GS03\",\"S-LH02\",\"S-LS01\",\"S-RK01\",\"S-SA02\",\"S-LS02\",\"S-SK01\",\"S-SP02\",\"S-WM02\",\"S-CC01\",\"S-FB02\",\"S-HE01\",\"S-SB01\",\"S-ML01\",\"S-TP02\",\"S-CH01\",\"S-PG01\",\"S-CP01\",\"S-GF01\",\"S-LY02\",\"S-GA01\"]}"
    val ttgWapJson = "{\"Hot\":[\"1052:FrogsNFliesH5:0\",\"1051:MadMonkeyH5:0\",\"1069:MoreMonkeysH5:0\",\"1058:LostTempleH5:0\",\"1070:DragonPalaceH5:0\",\"1053:ChilliGoldH5:0\",\"1055:DolphinGoldH5 :0\",\"1057:SilverLionH5:0\",\"1067:FivePiratesH5:0\",\"1105:WildWildWitch:0\",\"1087:YingCaiShen:0\",\"1052:FrogsNFliesH5:0\"],\"New\":[\"1083:MadMonkey2:0\",\"15400:EGIGame:0\",\"1136:GoldenClaw:0\",\"1082:FrogsNFlies2:0\",\"1114:WildWildTiger:0\",\"1120:GoldenBuffalo:0\",\"15408:EGIGame:0\",\"1106:WildKartRacers:0\",\"1084:MedusaCurse:0\",\"1130:DiamondFortune:0\",\"1112:UltimateFighter:0\",\"1125:Shark:0\",\"1046:Crazy8s:0\",\"1098:DiaDeMuertos:0\",\"1099:NeptunesGoldH5:0\",\"1107:KingDinosaur:0\"],\"\":[\"Slot\"],\"Slot\":[\"1052:FrogsNFliesH5:0\",\"1051:MadMonkeyH5:0\",\"1069:MoreMonkeysH5:0\",\"1058:LostTempleH5:0\",\"1070:DragonPalaceH5:0\",\"1053:ChilliGoldH5:0\",\"1055:DolphinGoldH5 :0\",\"1057:SilverLionH5:0\",\"1067:FivePiratesH5:0\",\"1114:WildWildTiger:0\",\"1087:YingCaiShen:0\",\"1082:FrogsNFlies2:0\",\"1118:LuckyPandaH5:0\",\"1103:HeroesNeverDie:0\",\"1109:FairyHollow:0\",\"1079:ReelsOfFortune:0\",\"1111:NeutronStarH5:0\",\"1132:ThreeDiamonds:0\",\"1133:GoldenPigNJ:0\",\"1077:GoldenAmazon:0\",\"1089:MonkeyLuck:0\",\"1072:SuperKids:0\",\"1078:Huluwa:0\",\"1075:DetectiveBlackCat:0\",\"1064:FireGoddessH5:0\",\"1068:ThunderingZeusH5:0\",\"1044:StacksOfCheese:0\",\"1047:DynastyEmpire:0\",\"1063:FuStarH5:0\",\"1066:AladdinsLegacyH5:0\",\"1080:GoldenDragon:0\",\"1101:GemRiches :0\",\"1102:WildTriads:0\",\"1100:TinyDoorGods:0\",\"1043:KungFuShowdown:0\",\"1091:BattleHeroes:0\",\"1104:DiamondTowerH5:0\",\"1042:EightImmortals :0\",\"1049:LegendOfLinkH5:0\",\"1054:YearOfTheMonkeyH5:0\",\"1056:DragonKingH5:0\",\"1060:FortunePaysH5:0\",\"1061:HotVolcanoH5:0\",\"1073:GongXiFaCai:0\",\"1083:MadMonkey2:0\",\"15400:EGIGame:0\",\"1136:GoldenClaw:0\",\"1120:GoldenBuffalo:0\",\"15408:EGIGame:0\",\"1106:WildKartRacers:0\",\"1084:MedusaCurse:0\",\"1130:DiamondFortune:0\",\"1112:UltimateFighter:0\",\"1125:Shark:0\",\"1046:Crazy8s:0\",\"1098:DiaDeMuertos:0\",\"1099:NeptunesGoldH5:0\",\"1107:KingDinosaur:0\"]}"
    val ttgWebJson = "{\"Hot\":[\"1079:ReelsOfFortune:0\",\"1077:GoldenAmazon:0\",\"1089:MonkeyLuck:0\",\"1072:SuperKids:0\",\"1078:Huluwa:0\",\"1075:DetectiveBlackCat:0\",\"1064:FireGoddessH5:0\",\"1069:MoreMonkeysH5:0\",\"1016:MadMonkey:0\",\"1052:FrogsNFliesH5:0\",\"1053:ChilliGoldH5:0\",\"1068:ThunderingZeusH5:0\",\"1058:LostTempleH5:0\",\"1057:SilverLionH5:0\",\"1070:DragonPalaceH5:0\",\"1025:LuckyPanda:0\"],\"New\":[\"1083:MadMonkey2:0\",\"1136:GoldenClaw:0\",\"1082:FrogsNFlies2:0\",\"1114:WildWildTiger:0\",\"1120:GoldenBuffalo:0\",\"1080:GoldenDragon:0\",\"1106:WildKartRacers:0\",\"1084:MedusaCurse:0\",\"1130:DiamondFortune:0\",\"1112:UltimateFighter:0\",\"1125:Shark:0\",\"1046:Crazy8s:0\",\"1098:DiaDeMuertos:0\",\"1099:NeptunesGoldH5:0\",\"1107:KingDinosaur:0\"],\"Slot\":[\"1079:ReelsOfFortune:0\",\"1077:GoldenAmazon:0\",\"1089:MonkeyLuck:0\",\"1072:SuperKids:0\",\"1078:Huluwa:0\",\"1075:DetectiveBlackCat:0\",\"1064:FireGoddessH5:0\",\"1069:MoreMonkeysH5:0\",\"1016:MadMonkey:0\",\"1052:FrogsNFliesH5:0\",\"1053:ChilliGoldH5:0\",\"1068:ThunderingZeusH5:0\",\"1058:LostTempleH5:0\",\"1057:SilverLionH5:0\",\"1070:DragonPalaceH5:0\",\"1025:LuckyPanda:0\",\"1118:LuckyPandaH5:0\",\"1103:HeroesNeverDie:0\",\"1109:FairyHollow:0\",\"1111:NeutronStarH5:0\",\"1132:ThreeDiamonds:0\",\"1133:GoldenPigNJ:0\",\"1044:StacksOfCheese:0\",\"1047:DynastyEmpire:0\",\"1063:FuStarH5:0\",\"1066:AladdinsLegacyH5:0\",\"1067:FivePiratesH5:0\",\"1080:GoldenDragon:0\",\"1101:GemRiches :0\",\"1087:YingCaiShen:0\",\"1102:WildTriads:0\",\"1100:TinyDoorGods:0\",\"1043:KungFuShowdown:0\",\"1091:BattleHeroes:0\",\"1104:DiamondTowerH5:0\",\"1024:TheSilkRoad:0\",\"1030:TikiTreasures:0\",\"1033:RobinHood:0\",\"1037:TigerSlayer:0\",\"1036:TheHoppingDead:0\",\"1040:DragonBallReels:0\",\"1042:EightImmortals :0\",\"1049:LegendOfLinkH5:0\",\"1054:YearOfTheMonkeyH5:0\",\"1003:DolphinGold:0\",\"1056:DragonKingH5:0\",\"1060:FortunePaysH5:0\",\"1061:HotVolcanoH5:0\",\"1073:GongXiFaCai:0\",\"526:FrogsNFlies:0\",\"1083:MadMonkey2:0\",\"533:ChilliGold:0\",\"486:ThunderingZeus:0\",\"1000:FortunePays:0\",\"484:LostTemple:0\",\"525:AladdinsLegacy:0\",\"480:VampiresVsWerewolves:0\",\"423:Dragon8sSlots:0\",\"1007:SilverLion:0\",\"411:FruitParty:0\",\"516:Taxi:0\",\"63:ArthursQuest:0\",\"478:SerengetiDiamonds:0\",\"477:AngelsTouch:0\",\"475:DracosFire:0\",\"1022:HotVolcano:0\",\"515:SamuraiPrincess:0\",\"444:BerryBlastSlots:0\",\"1039:RoseOfVenice:0\",\"1031:NeutronStar:0\",\"1034:Cleopatra:0\",\"1029:DragonPalace:0\",\"1027:AladdinHandOfMidas:0\",\"1026:Athena:0\",\"1011:JadeEmpire:0\",\"1035:YearOfTheMonkey:0\",\"1023:SnakeCharmer:0\",\"1028:GrandPrix:0\",\"1021:DragonKing:0\",\"1018:PotOGoldII:0\",\"1013:MonkeyAndTheMoon:0\",\"1017:ZeusVsHades:0\",\"1019:FireGoddess:0\",\"1009:KatLeeII:0\",\"1012:FivePirates:0\",\"1014:MoreMonkeys:0\",\"1004:JourneyWest:0\",\"1006:ActionHeroes:0\",\"1015:RedHotFreeSpins:0\",\"1008:CashGrabII:0\",\"1003:DolphinGold:0\",\"1002:ZodiacWilds:0\",\"1001:FuStar:0\",\"540:Fortune8Cat:0\",\"530:ChoySunDoa:0\",\"483:ShogunShowdown:0\",\"474:SinfulSpinsSlots:0\",\"473:BarsAndBellsSlots:0\",\"468:VictoryRidgeSlots:0\",\"462:ArthursQuestIISlots:0\",\"453:TheGreatCasiniSlots:0\",\"452:MagicalGroveSlots:0\",\"449:SurfsUpSlots:0\",\"447:TBSpinNWinSlots:0\",\"446:FortuneTellerSlots:0\",\"444:BerryBlastSlots:0\",\"440:KatLeeSlots:0\",\"439:LadysCharmsSlots:0\",\"438:VivaVeneziaSlots:0\",\"437:FanCashticSlots:0\",\"428:WildMummySlots:0\",\"424:PolarRichesSlots:0\",\"1083:MadMonkey2:0\",\"421:MonkeyLoveSlots:0\",\"416:NeptunesGoldSlots:0\",\"414:AmazonAdventureSlots:0\",\"413:JackpotHolidaySlots:0\",\"401:GoooalSlots:0\",\"1008:CashGrabII:0\",\"65:Oktoberfest:0\",\"64:BullsEyeBucks:0\",\"18:HoleInOne:0\",\"15:HollywoodReels:0\",\"10:FastTrack:0\",\"1136:GoldenClaw:0\",\"1082:FrogsNFlies2:0\",\"1114:WildWildTiger:0\",\"1120:GoldenBuffalo:0\",\"1080:GoldenDragon:0\",\"1106:WildKartRacers:0\",\"1084:MedusaCurse:0\",\"1130:DiamondFortune:0\",\"1112:UltimateFighter:0\",\"1125:Shark:0\",\"1046:Crazy8s:0\",\"1098:DiaDeMuertos:0\",\"1099:NeptunesGoldH5:0\",\"1107:KingDinosaur:0\"]}"



    fun addCategory(slotGames: List<SlotGame>, json: String): List<SlotGame> {

        val gameMap = slotGames.map { it.gameId to it }.toMap()

        val objectMapper = jacksonObjectMapper()
        val hots = hashSetOf<String>()
        val news = hashSetOf<String>()
        return objectMapper.readValue<Map<String, List<String>>>(json).filter { it.key.isNotBlank() } .map { a ->

            val category = GameCategory.valueOf(a.key)
            a.value.filter { gameMap[it] != null }.map {
                val game= gameMap[it]!!
                game.copy(category = category)

            }
        }.reduce { acc, list ->  acc.plus(list)}.map {

            if (it.category == GameCategory.Hot) {
                hots.add(it.gameId)
            } else if (it.category == GameCategory.New) {
                news.add(it.gameId)
            }

            val hot = hots.contains(it.gameId)
            val new = news.contains(it.gameId)
            it.copy(hot = hot, new = new)
        }
    }

}

fun main() {

    val name = "ttg_web.Done.csv"

    val file = File("/Users/cabbage/workspace/onepiece/treasure/gamefile/${name}")

    val data = file.readLines().filter { it.trim() != "," && it.trim() != "" }.map {
        val (name, category) = it.split(",")
        name to category
    }.groupBy { it.second }.map {
        it.key to it.value.map { it.first }
    }.toMap()

    val json = jacksonObjectMapper().writeValueAsString(data)

    println(json)
}
