package com.onepiece.treasure.games.slot

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.games.bet.MapUtil

object TTGGames {

    private val cvs = """
        1,Golden  Genie,黄金精灵,1108,GoldenGenie,0,GoldenGenie,95.39%,HTML5,Mobile/Web,1024 Ways Slot,19th November 2019,Yes,Slot,Medium,7600,
        2,Jawz,大白鲨,1143,Jawz,0,Jawz,94.88%,HTML5,Mobile/Web,6 Grab Zones,12th November 2019,NO,Slot,High,1800,
        3,Wild Wild Witch,超疯狂女巫,1105,WildWildWitch,0,WildWildWitch,95.01%,HTML5,Mobile/Web,40 Lines,16th October 2019,Yes,Slot,High,536220,
        4,Lucky Panda H5,熊猫陛下,1118,LuckyPandaH5,0,LuckyPandaH5,94.00%,HTML5,Mobile/Web,100 Lines,12th September 2019,Yes,Slot,Medium,100000,
        5,Wild Wild Tiger,虎虎生财,1114,WildWildTiger,0,WildWildTiger,95.00%,HTML5,Mobile/Web,25 Lines,14th August 2019,Yes,Slot,High,33550,
        6,Frogs 'n Flies 2,捕蝇大赛2,1082,FrogsNFlies2,0,FrogsNFlies2,90.00%,HTML5,Mobile/Web,1024 Ways Slot,23th July 2019,NO,Slot,Medium ,50000,
        7,Golden Claw,抓乐霸,1136,GoldenClaw,0,GoldenClaw,94.73%,HTML5,Mobile/Web,6 Grab Zones,27th June 2019,NO,Slot,High,1800,
        8,Mad Monkey 2,疯狂的猴子2,1083,MadMonkey2,0,MadMonkey2,95.00%,HTML5,Mobile/Web,40 Lines,11th June 2019,Yes,Slot,High,536220,
        9,Mammoth,长毛象,15400,EGIGame,0,RK_Mammoth,96.20%,HTML5,Mobile/Web,1024 Ways Slot,28th May 2019,Yes,Slot,High,2722 Times Bet,
        10,Wild Kart Racers,狂热卡丁赛车手,1106,WildKartRacers,0,WildKartRacers,95.00%,HTML5,Mobile/Web,25 Lines,14th May 2019,Yes,Slot,Medium ,125000,
        11,888 Golden Dragon,发发发金龙,15408,EGIGame,0,RK_infinitydragons,96.40%,HTML5,Mobile/Web,1 line,26th April 2019,Yes,Slot,Medium ,250 Times Bet,
        12,Fairy Hollow,小精灵寻宝记,1109,FairyHollow,0,FairyHollow,93.00%,HTML5,Mobile/Web,25 Lines,16th April 2019 ,Yes,Slot,Medium ,12500,
        13,Golden Buffalo,黄金野牛,1120,GoldenBuffalo,0,GoldenBuffalo,95.00%,HTML5,Mobile/Web,243 Ways Slot,1st April 2019 ,Yes,Slot,Medium ,22500,
        14,Heroes Never Die,英雄不朽,1103,HeroesNeverDie,0,HeroesNeverDie,96.00%,HTML5,Mobile/Web,20 Lines ,13th March 2019,Yes,Slot,High,37950,
        15,Golden Pig,金猪,1133,GoldenPigNJ,0,GoldenPigNJ,95.00%,HTML5,Mobile/Web,8 Lines,14th February 2019,Yes,Slot,Medium,120000,
        16,3 Diamonds,钻钻钻,1132,ThreeDiamonds,0,ThreeDiamonds,95.19%,HTML5,Mobile/Web,9 Lines,13th February 2019,Yes,Slot,Medium ,24670,
        17,Neutron Star H5 ,中子星H5,1111,NeutronStarH5,0,NeutronStarH5,93.12%,HTML5,Mobile/Web,10 Lines ,15th January 2019,Yes,Slot,High,50000,
        18,Medusa's Curse,美杜莎的诅咒,1084,MedusaCurse,0,MedusaCurse,95.00%,HTML5,Mobile/Web,25 Lines ,26th December 2018,Yes,Slot,High,33550,
        19,Diamond Fortune ,钻石神偷,1130,DiamondFortune,0,DiamondFortune,97.00%,HTML5,Mobile/Web,1024 Ways Slot,20th December 2018,Yes,Slot,Low,10020,
        20,Ultimate fighter,终极格斗手,1112,UltimateFighter,0,UltimateFighter,96.00%,HTML5,Mobile/Web,720 Ways Slot,20th December 2018,Yes,Slot,Low,551875,
        21,Dragon Palace H5,龙宫 H5,1070,DragonPalaceH5,0,DragonPalaceH5,96.00%,HTML5,Mobile/Web,243 Ways Slot,20th December 2018,Yes,Slot,Medium ,22500,
        22,Shark,鲨鱼总动员,1125,Shark,0,Shark,95.19%,HTML5,Mobile/Web,9 Lines,4th December 2018,Yes,Slot,Medium,24670,
        23,Crazy8s,疯狂8,1046,Crazy8s,0,Crazy8s,95.00%,HTML5,Mobile/Web,25 Lines ,4th December 2018,Yes,Slot,Medium,125000,
        24,Dia De Muertos,死亡日,1098,DiaDeMuertos,0,DiaDeMuertos,96.00%,HTML5,Mobile/Web,243 Ways Slot,31st October 2018,Yes,Slot,Low,70000,
        25,Neptune's Gold H5,海神秘宝 H5,1099,NeptunesGoldH5,0,NeptunesGoldH5,97.00%,HTML5,Mobile/Web,20 Lines,2nd October 2018,Yes,Slot,High,334200,
        26,King Dinosaur,恐龙之王,1107,KingDinosaur,0,KingDinosaur,93.00%,HTML5,Mobile/Web,1024 Ways Slot,20th September 2018,Yes,Slot,Low,337550,
        27,Kung Fu Showdown,功夫传奇对决,1043,KungFuShowdown,0,KungFuShowdown,93.00%,HTML5,Mobile/Web,10 Lines,23th August 2018,Yes,Slot,High,50000,
        28,Battle Heroes,英雄之战,1091,BattleHeroes,0,BattleHeroes,95.00%,HTML5,Mobile/Web,1024 Ways Slot,15th August 2018,Yes,Slot,Low,337550,
        29,Diamond Tower H5,钻石塔 H5,1104,DiamondTowerH5,0,DiamondTowerH5,97.00%,HTML5,Mobile/Web,1024 Ways Slot,9th July 2018,Yes,Slot,Low,10020,
        30,Lost Temple H5,失落的神庙 H5,1058,LostTempleH5,0,LostTempleH5,97.00%,HTML5,Mobile/Web,1024 Ways Slot,18th June 2018,Yes,Slot,Low,10020,
        31,Gem Riches ,宝石财富,1101,GemRiches ,0,GemRiches,97.00%,HTML5,Mobile/Web,243 Ways Slot,7th June 2018,Yes,Slot,Low,70000,
        32,Ying Cai Shen,迎财神,1087,YingCaiShen,0,YingCaiShen,97.00%,HTML5,Mobile/Web,243 Ways Slot,7th June 2018,Yes,Slot,Medium,10000,
        33,Wild Triads,古惑仔,1102,WildTriads,0,WildTriads,95.00%,HTML5,Mobile/Web,20 Lines,31st May 2018,Yes,Slot,High,37950,
        34,Tiny Door Gods,小门神,1100,TinyDoorGods,0,TinyDoorGods,95.00%,HTML5,Mobile/Web,25 Lines,23th April 2018,Yes,Slot,Medium,125000,
        35,Golden Dragon,金龙,1080,GoldenDragon,0,GoldenDragon,95.00%,HTML5,Mobile/Web,1024 Ways Slot,5th April 2018,Yes,Slot,Low,37400,
        36,Cherry Fortune,财富小樱桃,1092,CherryFortune,0,CherryFortune,90.00%,HTML5,Mobile/Web,1 Line,26th March 2018,No,Slot,Low,400,
        37,Reels Of Fortune,财富转转转,1079,ReelsOfFortune,0,ReelsOfFortune,95.00%,HTML5,Mobile/Web,25 Lines,19th March 2018,Yes,Slot,Medium,42180,
        38,Golden Amazon,金色亚马逊,1077,GoldenAmazon,0,GoldenAmazon,95.00%,HTML5,Mobile/Web,720 Ways Slot,1st March 2018,Yes,Slot,Low/Medium,50625,
        39,Monkey Luck,吉祥猴子,1089,MonkeyLuck,0,MonkeyLuck,95.00%,HTML5,Mobile/Web,1 Line,20th February 2018,No,Slot,Low,400,
        40,League Of Champions,英雄联盟,1086,LeagueOfChampions,0,LeagueOfChampions,97.00%,HTML5,Mobile/Web,1024 Ways Slot,5th February 2018,Yes,Slot,Low,26500,
        41,ThunderingZeus H5,天神宙斯 H5,1068,ThunderingZeusH5,0,ThunderingZeusH5,97.00%,HTML5,Mobile/Web,20 Lines,18th January 2018,Yes,Slot,High,306250,
        42,Mad Monkey H5,疯狂的猴子 H5,1051,MadMonkeyH5,0,MadMonkeyH5,95.00%,HTML5,Mobile/Web,50 Lines,1st July 2017,Yes,Slot,High,187500,
        43,Fire Goddess H5,美女火神 H5,1064,FireGoddessH5,0,FireGoddessH5,95.00%,HTML5,Mobile/Web,1024 Ways Slot,6th November 2017,Yes,Slot,Medium,37400,
        44,Dynasty Empire,帝國王朝,1047,DynastyEmpire,0,DynastyEmpire,97.00%,HTML5,Mobile/Web,1024 Ways Slot,2nd November 2017,Yes,Slot,Medium,26500,
        45,Aladdin's Legacy H5,阿拉丁神迹 H5,1066,AladdinsLegacyH5,0,AladdinsLegacyH5,95.00%,HTML5,Mobile/Web,20 Lines,18th October 2017,Yes,Slot,Medium,16200,
        46,Fu Star H5,福星高照 H5,1063,FuStarH5,0,FuStarH5,95.00%,HTML5,Mobile/Web,25 Lines,18th October 2017,Yes,Slot,High,12500,
        47,Five Pirates H5,五个海盗 H5,1067,FivePiratesH5,0,FivePiratesH5,95.00%,HTML5,Mobile/Web,1024 Ways Slot,3rd October 2017,Yes,Slot,Medium,150000,
        48,More Monkeys H5,更多猴子 H5,1069,MoreMonkeysH5,0,MoreMonkeysH5,95.00%,HTML5,Mobile/Web,1024 Ways Slot,20th September 2017,Yes,Slot,Medium,54600,
        49,Stacks of Cheese,堆叠奶酪,1044,StacksOfCheese,0,StacksOfCheese,95.00%,HTML5,Mobile/Web,30 Lines,22nd June 2017,Yes,Slot,Medium,65500,
        50,Super Kids,超级宝贝,1072,SuperKids,0,SuperKids,95.00%,HTML5,Mobile/Web,25 Lines,26th April 2017,Yes,Slot,Medium,125000,
        51,Hot Volcano H5,炽热火山 H5,1061,HotVolcanoH5,0,HotVolcanoH5,95.00%,HTML5,Mobile/Web,25 Lines,21st June 2017,Yes,Slot,Low,12320,
        52,Fortune Pays H5,招财进宝 H5,1060,FortunePaysH5,0,FortunePaysH5,95.00%,HTML5,Mobile/Web,50 Lines,4th May 2017,Yes,Slot,High,148700,
        53,Huluwa,葫芦娃,1078,Huluwa,0,Huluwa,95.00%,HTML5,Mobile/Web,30 Lines,17th April 2017,Yes,Slot,Medium,29625,
        54,Legend Of Link H5,林克的传说 H5,1049,LegendOfLinkH5,0,LegendOfLinkH5,95.00%,HTML5,Mobile/Web,25 Lines,20th March 2017,Yes,Slot,High,31250,
        55,Detective Black Cat,黑猫警长,1075,DetectiveBlackCat,0,DetectiveBlackCat,95.00%,HTML5,Mobile/Web,40 Lines,2nd March 2017,Yes,Slot,Medium,67700,
        56,Frogs N Flies H5,捕蝇大赛 H5,1052,FrogsNFliesH5,0,FrogsNFliesH5,97.00%,HTML5,Mobile/Web,1024 Ways Slot,16th February 2017,Yes,Slot,Low,26500,
        57,Eight Immortals,八仙过海,1042,EightImmortals ,0,EightImmortals,95.00%,HTML5,Mobile/Web,25 Lines,14th February 2017,Yes,Slot,High,1584000,
        58,Gong Xi Fa Cai,恭喜发财,1073,GongXiFaCai,0,GongXiFaCai,95.00%,HTML5,Mobile/Web,25 Lines,14th February 2017,Yes,Slot,High,1584000,
        59,Dolphin Gold H5,金海豚 H5,1055,DolphinGoldH5 ,0,DolphinGoldH5,95.00%,HTML5,Mobile/Web,40 Lines,17th January 2017,Yes,Slot,Medium,67700,
        60,Year of The Monkey H5,猴年大吉 H5,1054,YearOfTheMonkeyH5,0,YearOfTheMonkeyH5,95.00%,HTML5,Mobile/Web,40 Lines,29th November 2016,Yes,Slot,Medium,200640,
        61,Chilli Gold H5,火辣金砖 H5,1053,ChilliGoldH5,0,ChilliGoldH5,95.00%,HTML5,Mobile/Web,40 Lines,29th November 2016,Yes,Slot,Medium,67700,
        62,Silver Lion H5,银狮奖 H5,1057,SilverLionH5,0,SilverLionH5,95.00%,HTML5,Mobile/Web,1024 Ways Slot,8th December 2016,Yes,Slot,Medium,7600,
        63,Dragon King H5,海龙王 H5,1056,DragonKingH5,0,DragonKingH5,95.00%,HTML5,Mobile/Web,25 Lines,8th December 2016,Yes,Slot,Low,31250,
        64,DragonBallReels,龍珠,1040,DragonBallReels,0,DragonBallReels,95.00%,Flash,Web,25 Lines,1st July 2016,Yes,Slot,Low,#N/A,
        65,RoseOfVenice ,威尼斯野玫瑰,1039,RoseOfVenice,0,RoseOfVenice,95.00%,Flash,Web,25 Lines, 15th June 2016,Yes,Slot,Low,#N/A,
        66,BerryBlastPlus,水果对对碰,1038,BerryBlastPlus,0,BerryBlastPlus,95.00%,Flash,Web,243 Ways Slot,1st August 2016,Yes,Slot,High,#N/A,
        67,TigerSlayer,武松打虎,1037,TigerSlayer,0,TigerSlayer,97.00%,Flash,Web,25 Lines,15th September 2016,Yes,Slot,High,#N/A,
        68,TheHoppingDead,僵尸先生,1036,TheHoppingDead,0,TheHoppingDead,95.00%,Flash,Web,25 Lines,14th July 2016,Yes,Slot,Medium,#N/A,
        69,YearofTheMonkey,猴年大吉,1035,YearOfTheMonkey,0,YearOfTheMonkey,95.00%,Flash,Web,40 Lines,-,Yes,Slot,Medium,#N/A,
        70,Cleopatra,埃及艳后,1034,Cleopatra,0,Cleopatra,95.00%,Flash,Web,25 Lines,17th May 2016,Yes,Slot,Medium,#N/A,
        71,RobinHood,罗宾汉,1033,RobinHood,0,RobinHood,95.00%,Flash,Web,40 Lines,3rd October 2016,Yes,Slot,Medium,#N/A,
        72,NeutronStar,中子星,1031,NeutronStar,0,NeutronStar,95.00%,Flash,Web,10 Lines,1st June 2016,Yes,Slot,Low,#N/A,
        73,TikiTreasures,图腾宝藏,1030,TikiTreasures,0,TikiTreasures,95.00%,Flash,Web,100 Lines,30th August 2016,Yes,Slot,High,#N/A,
        74,DragonPalace,龍宮,1029,DragonPalace,0,DragonPalace,95.00%,Flash,Web,243 Ways Slot,31st March 2016,Yes,Slot,Medium/High,#N/A,
        75,GrandPrix,国际赛车,1028,GrandPrix,0,GrandPrix,95.00%,Flash,Web,243 Ways Slot,-,Yes,Slot,Low,#N/A,
        76,AladdinHandOfMidas,阿拉丁-点金手,1027,AladdinHandOfMidas,0,AladdinHandOfMidas,95.00%,Flash,Web,25 Lines,23th March 2016,Yes,Slot,Medium/Low,#N/A,
        77,Athena,雅典娜,1026,Athena,0,Athena,95.00%,Flash,Web,40 Lines,10th March 2016,Yes,Slot,Medium,#N/A,
        78,LuckyPanda,幸运熊猫,1025,LuckyPanda,0,LuckyPanda,97.00%,Flash,Web,100 Lines,1st May 2016,Yes,Slot,Medium,#N/A,
        79,TheSilkRoad,丝绸之路,1024,TheSilkRoad,0,TheSilkRoad,95.00%,Flash,Web,243 Ways Slot,17th August 2016,Yes,Slot,Medium,#N/A,
        80,SnakeCharmer,耍蛇者,1023,SnakeCharmer,0,SnakeCharmer,95.00%,Flash,Web,25 Lines,-,Yes,Slot,Low,#N/A,
        81,HotVolcano,炽热火山,1022,HotVolcano,0,HotVolcano,95.00%,Flash,Web,25 Lines,-,Yes,Slot,Low,#N/A,
        82,Drago King,海龍王,1021,DragonKing,0,DragonKing,95.00%,Flash,Web,25 Lines,-,Yes,Slot,Low,#N/A,
        83,FireGoddess,美女火神,1019,FireGoddess,0,FireGoddess,95.00%,Flash,Web,1024 Ways Slot,-,Yes,Slot,Medium,#N/A,
        84,PotOGoldII,金罐子II,1018,PotOGoldII,0,PotOGoldII,95.00%,Flash,Web,25 Lines,-,Yes,Slot,Low,#N/A,
        85,ZeusVsHades,天神VS冥王,1017,ZeusVsHades,0,ZeusVsHades,95.00%,Flash,Web,25 Lines,-,Yes,Slot,High,#N/A,
        86,MadMonkey,疯狂的猴子,1016,MadMonkey,0,MadMonkey,95.00%,Flash,Web,50 Lines,,Yes,Slot,High,#N/A,
        87,RedHotFreeSpins,惹火的自由旋转,1015,RedHotFreeSpins,0,RedHotFreeSpins,95.00%,Flash,Web,243 Ways Slot,-,Yes,Slot,Low,#N/A,
        88,MoreMonkeys,更多猴子,1014,MoreMonkeys,0,MoreMonkeys,95.00%,Flash,Web,1024 Ways Slot,-,Yes,Slot,Medium,#N/A,
        89,Monkey AndTheMoon,猴子捞月,1013,MonkeyAndTheMoon,0,MonkeyAndTheMoon,95.00%,Flash,Web,40 Lines,-,Yes,Slot,Medium,#N/A,
        90,FivePirates,五个海盗,1012,FivePirates,0,FivePirates,95.00%,Flash,Web,1024 Ways Slot,-,Yes,Slot,Medium,#N/A,
        91,JadeEmpire,翡翠帝国,1011,JadeEmpire,0,JadeEmpire,95.00%,Flash,Web,60 Lines,-,Yes,Slot,Medium,#N/A,
        92,KatLeeII,吉李 II:赏金猎人,1009,KatLeeII,0,KatLeeII,95.00%,Flash,Web,40 Lines,-,Yes,Slot,High,#N/A,
        93,CashGrabII,快抓钱2,1008,CashGrabII,0,CashGrabII,97.00%,Flash,Web,1024 Ways Slot,-,Yes,Slot,Medium,#N/A,
        94,SilverLion,银狮奖,1007,SilverLion,0,SilverLion,95.00%,Flash,Web,1024 Ways Slot,-,Yes,Slot,Medium,#N/A,
        95,ActionHeroes ,战斗英雄,1006,ActionHeroes,0,ActionHeroes,95.00%,Flash,Web,40 Lines,-,Yes,Slot,High,#N/A,
        96,JourneyWest,西游记,1004,JourneyWest,0,JourneyWest,97.00%,Flash,Web,40 Lines,-,Yes,Slot,Low,#N/A,
        97,DolphinGold,金海豚,1003,DolphinGold,0,DolphinGold,95.00%,Flash,Web,40 Lines,-,Yes,Slot,Medium,#N/A,
        98,ZodiacWild,十二生肖,1002,ZodiacWilds,0,ZodiacWilds,95.00%,Flash,Web,50 Lines,-,Yes,Slot,High,#N/A,
        99,FuStar,福星高照,1001,FuStar,0,FuStar,95.00%,Flash,Web,25 Lines,-,Yes,Slot,High,#N/A,
        100,FortunePays,招财进宝,1000,FortunePays,0,FortunePays,95.00%,Flash,Web,50 Lines,-,Yes,Slot,High,#N/A,
        101,Fortune8Cat,8 招财猫,540,Fortune8Cat,0,Fortune8Cat,95.00%,Flash,Web,1024 Ways Slot,-,Yes,Slot,-,#N/A,
        102,ChilliGold,火辣金砖,533,ChilliGold,0,ChilliGold,95.00%,Flash,Web,40 Lines,-,Yes,Slot,Medium,#N/A,
        103,ChoySunDoa,财神到,530,ChoySunDoa,0,ChoySunDoa,94.00%,Flash,Web,243 Ways Slot,,Yes,Slot,-,#N/A,
        104,FrogsNFlies,捕蝇大赛,526,FrogsNFlies,0,FrogsNFlies,97.00%,Flash,Web,1024 Ways Slot,-,Yes,Slot,Low,#N/A,
        105,AladdinsLegacy,阿拉丁神迹,525,AladdinsLegacy,0,AladdinsLegacy,95.00%,Flash,Web,20 Lines,-,Yes,Slot,Medium,#N/A,
        106,Taxi,计程车,516,Taxi,0,Taxi,96.00%,Flash,Web,25 Lines,-,Yes,Slot,Low,#N/A,
        107,SamuraiPrincess,外道姬,515,SamuraiPrincess,0,SamuraiPrincess,97.00%,Flash,Web,40 Lines,-,Yes,Slot,-,#N/A,
        108,ThunderingZeus,天神宙斯,486,ThunderingZeus,0,ThunderingZeus,97.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        109,LostTemple,失落的神庙,484,LostTemple,0,LostTemple,97.00%,Flash,Web,1024 Ways Slot,-,Yes,Slot,-,#N/A,
        110,ShogunShowdown,幕府摊牌,483,ShogunShowdown,0,ShogunShowdown,95.00%,Flash,Web,50 Lines,-,Yes,Slot,Low,#N/A,
        111,VampiresVsWerewolves,吸血鬼大战狼人,480,VampiresVsWerewolves,0,VampiresVsWerewolves,98.00%,Flash,Web,20 Lines,-,No,Slot,-,#N/A,
        112,SerengetiDiamonds,塞伦盖提之钻,478,SerengetiDiamonds,0,SerengetiDiamonds,97.00%,Flash,Web,25 Lines,-,Yes,Slot,High,#N/A,
        113,AngelsTouch,天使的触摸,477,AngelsTouch,0,AngelsTouch,96.50%,Flash,Web,40 Lines,-,Yes,Slot,High,#N/A,
        114,DracosFire,天龙火焰,475,DracosFire,0,DracosFire,95.00%,Flash,Web,50 Lines,-,Yes,Slot,Low,#N/A,
        115,Sinful Spins Slots,万恶旋转,474,SinfulSpinsSlots,0,SinfulSpinsSlots,96.00%,Flash,Web,20 Lines,-,Yes,Slot,Low,#N/A,
        116,Bars And Bells Slots,酒吧门铃,473,BarsAndBellsSlots,0,BarsAndBellsSlots,95.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        117,VictoryRidgeSlots,维多利山脊,468,VictoryRidgeSlots,0,VictoryRidgeSlots,94.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        118,Arthurs Quest II,亚瑟寻宝之旅 II,462,ArthursQuestIISlots,0,ArthursQuestIISlots,94.00%,Flash,Web,9 Lines,-,No,Slot,Low,#N/A,
        119,TheGreatCasiniSlots,大卡西尼,453,TheGreatCasiniSlots,0,TheGreatCasiniSlots,94.00%,Flash,Web,15 Lines,-,Yes,Slot,High,#N/A,
        120,MagicalGroveSlots,魔法森林,452,MagicalGroveSlots,0,MagicalGroveSlots,94.00%,Flash,Web,15 Lines,-,Yes,Slot,High,#N/A,
        121,SurfsUpSlots,冲浪,449,SurfsUpSlots,0,SurfsUpSlots,94.00%,Flash,Web,15 Lines,-,Yes,Slot,High,#N/A,
        122,TBSpinNWinSlots,三重红利幸运大转盘,447,TBSpinNWinSlots,0,TBSpinNWinSlots,94.00%,Flash,Web,30 Lines,-,Yes,Slot,High,#N/A,
        123,FortuneTellerSlots,算命嬴大奖,446,FortuneTellerSlots,0,FortuneTellerSlots,94.00%,Flash,Web,15 Lines,-,Yes,Slot,High,#N/A,
        124,BerryBlastSlots,浆果培击,444,BerryBlastSlots,0,BerryBlastSlots,94.00%,Flash,Web,30 Lines,-,No,Slot,High,#N/A,
        125,KatLeeSlots,吉李：赏金猎人,440,KatLeeSlots,0,KatLeeSlots,94.00%,Flash,Web,30 Lines,-,Yes,Slot,High,#N/A,
        126,LadysCharmsSlots,淑女魅力,439,LadysCharmsSlots,0,LadysCharmsSlots,94.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        127,VivaVeneziaSlots,威尼斯万岁,438,VivaVeneziaSlots,0,VivaVeneziaSlots,94.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        128,FanCashticSlots,奇妙嬴现金,437,FanCashticSlots,0,FanCashticSlots,94.00%,Flash,Web,10 Lines,-,No,Slot,Low,#N/A,
        129,WildMummySlots,疯狂木乃伊,428,WildMummySlots,0,WildMummySlots,94.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        130,PolarRichesSlots,极地财宝,424,PolarRichesSlots,0,PolarRichesSlots,94.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        131,Dragon8 Slots,龙8,423,Dragon8sSlots,0,Dragon8sSlots,94.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        132,MonkeyLoveSlots,猴恋,421,MonkeyLoveSlots,0,MonkeyLoveSlots,94.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        133,NeptunesGoldSlots,海王星的金,416,NeptunesGoldSlots,0,NeptunesGoldSlots,94.00%,Flash,Web,20 Lines,-,Yes,Slot,High,#N/A,
        134,AmazonAdventureSlots,亚马逊奇遇记,414,AmazonAdventureSlots,0,AmazonAdventureSlots,94.00%,Flash,Web,10 Lines,-,No,Slot,Low,#N/A,
        135,JackpotHolidaySlots,头彩假日,413,JackpotHolidaySlots,0,JackpotHolidaySlots,94.00%,Flash,Web,10 Lines,-,No,Slot,Low,#N/A,
        136,FruitParty,水果会,411,FruitParty,0,FruitParty,94.00%,Flash,Web,9 Lines,-,No,Slot,Low,#N/A,
        137,GoooalSlots,足球赛事,401,GoooalSlots,0,GoooalSlots,95.00%,Flash,Web,1/3 Lines,-,No,Slot,Low,#N/A,
        138,CashGrab,快抓钱,391,GenericSlots,21,CashGrab,92.00%,Flash,Web,1/3 Lines,-,No,Slot,Low,#N/A,
        139,Oktoberfest,啤酒节,65,Oktoberfest,0,Oktoberfest,94.00%,Flash,Web,9 Lines,-,No,Slot,Low,#N/A,
        140,BullsEyeBucks,牛眼钞票,64,BullsEyeBucks,0,BullsEyeBucks,94.00%,Flash,Web,9 Lines,-,No,Slot,Low,#N/A,
        141,ArthursQuest,亚瑟寻宝之旅 I,63,ArthursQuest,0,ArthursQuest,94.00%,Flash,Web,9 Lines,-,No,Slot,Low,#N/A,
        142,JumpForGold,奔向黄金,57,GenericSlots,10,JumpForGold,87.00%,Flash,Web,1/3 Lines,-,No,Slot,Low,#N/A,
        143,Cash Inferno,现金地狱,40,FiveReelSlots,0,FiveReelSlots,94.00%,Flash,Web,9 Lines,-,No,Slot,Low,#N/A,
        144,HoleInOne,一杆进洞,18,HoleInOne,0,HoleInOne,93.00%,Flash,Web,9 Lines,-,No,Slot,-,#N/A,
        145,Lucky_Cherry,幸运樱桃,17,GenericSlots,11,Lucky_Cherry,92.00%,Flash,Web,1/3 Lines,-,No,Slot,Low,#N/A,
        146,Space Invasion,太空入侵,16,GenericSlots,20,Space,92.00%,Flash,Web,1/3 Lines,-,No,Slot,Low,#N/A,
        147,HollywoodReels,好莱坞明星,15,HollywoodReels,0,HollywoodReels,90.00%,Flash,Web,9 Lines,-,No,Slot,Low,#N/A,
        148,Wild_West,狂野西部,11,GenericSlots,16,Wild_West,92.00%,Flash,Web,1/3 Lines,-,No,Slot,Low,#N/A,
        149,FastTrack,赛车快道,10,FastTrack,0,FastTrack,90.00%,Flash,Web,9 Lines,-,No,Slot,Low,#N/A,
        150,Pirate_Treasure,海盗宝库,9,GenericSlots,17,Pirate_Treasure,92.00%,Flash,Web,1/3 Lines,-,No,Slot,Low,#N/A,
        151,PerfectPairsBlackjack,完美对子 21 点,454,PerfectPairsBlackjack,0,PerfectPairsBlackjack,N/A,Flash,Web,-,-,-,Card Games,-,0,在AMS下架了
        152,ThreeCardPoker,三张牌扑克,32,ThreeCardPoker,0,ThreeCardPoker,N/A,Flash,Web,-,-,-,Card Games,-,0,在AMS下架了
        153,Lucky7Blackjack,幸运７的２１点,25,Lucky7Blackjack,0,Lucky7Blackjack,N/A,Flash,Web,-,-,-,Card Games,-,0,在AMS下架了
        154,Baccarat,百家乐,13,Baccarat,0,Baccarat,N/A,Flash,Web,-,-,-,Card Games,-,0,在AMS下架了
        155,Roulette,轮盘,8,Roulette,0,Roulette,N/A,Flash,Web,-,-,-,Table Games,-,0,在AMS下架了
        156,CasinoStudPoker,加勒比梭哈扑克,6,CasinoStudPoker,0,CasinoStudPoker,N/A,Flash,Web,-,-,-,Card Games,-,0,在AMS下架了
        157,Blackjack,21点,5,Blackjack,0,Blackjack,N/A,Flash,Web,-,-,-,Card Games,-,0,在AMS下架了
        158,All_American,全美模式,4,VideoPoker,4,All_American,97.00%,Flash,Web,-,-,-,Video Poker,-,0,在AMS下架了
        159,Joker_Poker,王牌扑克,3,VideoPoker,3,Joker_Poker,95.70%,Flash,Web,-,-,-,Video Poker,-,0,在AMS下架了
        160,Deuces_Wild,狂野牌２号,2,VideoPoker,2,Deuces_Wild,96.70%,Flash,Web,-,-,-,Video Poker,-,0,在AMS下架了
        161,Jacks_or_Better,杰克高手,1,VideoPoker,1,Jacks_or_Better,97.10%,Flash,Web,-,-,-,Video Poker,-,0,在AMS下架了
    """.trimIndent()

    val mobileGames: List<SlotGame>
    val pcGames: List<SlotGame>
//    val gameMapUtils: Map<String, MapUtil>

    init {
        val list = cvs.lines().map {

            val list = it.split(",")
            val englishGameName = list[1]
            val chineseGameName = list[2]
            val gameId = list[3]
            val gameName = list[4]
            val gameType = list[5]
            val platforms = list[9]
            val category = list[13]

            val map = mapOf(
                    "gameId" to "$gameId:$gameName:$gameType",
                    "gameName" to gameName,
                    "englishName" to englishGameName,
                    "chineseName" to chineseGameName,
                    "gameType" to gameType,
                    "platforms" to platforms,
                    "category" to category
            )

            MapUtil.instance(map)
            // http://ams-games.stg.ttms.co/player/assets/images/games/1020.png
            // http://ams-games.stg.ttms.co/player/assets/images/games/tall/1020.jpg
        }.filter { it.asString("category") == "Slot" }

        mobileGames = list.filter { it.asString("platforms").contains("Mobile") }.map {
            val gameId = it.asString("gameId")
            SlotGame(gameId = gameId, gameName = it.asString("englishName"), chineseGameName = it.asString("chineseName"),
                    category = GameCategory.SLOT, hot = false, new = false, icon = "http://ams-games.stg.ttms.co/player/assets/images/games/${gameId}.png",
                    status = Status.Normal, touchIcon = "http://ams-games.stg.ttms.co/player/assets/images/games/${gameId}.png", platform = Platform.TTG)
        }

        pcGames = list.filter { it.asString("platforms").contains("Web") }.map {
            val gameId = it.asString("gameId")
            SlotGame(gameId = gameId, gameName = it.asString("gameName"), chineseGameName = it.asString("chineseName"),
                    category = GameCategory.SLOT, hot = false, new = false, icon = "http://ams-games.stg.ttms.co/player/assets/images/games/${gameId}.png",
                    status = Status.Normal, touchIcon = "http://ams-games.stg.ttms.co/player/assets/images/games/${gameId}.png", platform = Platform.TTG)
        }

//        gameMapUtils = list.map { it.asString("gameId") to it }.toMap()
    }

//    fun getNameAndType(gameId: String): Pair<String, String> {
//        val mapUtil = gameMapUtils[gameId]!!
//        return mapUtil.asString("gameName") to mapUtil.asString("gameType")
//    }

}