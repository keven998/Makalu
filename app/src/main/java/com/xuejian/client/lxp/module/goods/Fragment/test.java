//package com.xuejian.client.lxp.module.goods.Fragment;
//
//import com.alibaba.fastjson.JSON;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
///**
// * Created by yibiao.qin on 2015/12/24.
// */
//public class test {
//    public static void main(String [] args){
//        //test();
//        System.out.println(formatDuring(1453048136000l-System.currentTimeMillis()));
//    }
//
//    public static String formatDuring(long mss) {
//        long day = mss /  86400000;
//        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
//        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
//        long seconds = (mss % (1000 * 60)) / 1000;
//        return String.valueOf(day)+day+"天"+hours + " 小时 " + minutes + " 分 "
//                + seconds + " 秒 ";
//    }
//    public static void test(){
//        String string = "AL, 阿尔巴尼亚, 355\n" +
//                "DZ, 阿尔及利亚, 213\n" +
//                "AF, 阿富汗, 93\n" +
//                "AR, 阿根廷, 54\n" +
//                "AE, 阿联酋, 971\n" +
//                "AW, 阿鲁巴, 297\n" +
//                "OM, 阿曼, 968\n" +
//                "AZ, 阿塞拜疆, 994\n" +
//                "IE, 爱尔兰, 353\n" +
//                "EG, 埃及, 20\n" +
//                "ET, 埃塞俄比亚, 251\n" +
//                "EE, 爱沙尼亚, 372\n" +
//                "AD, 安道尔, 376\n" +
//                "AO, 安哥拉, 244\n" +
//                "AI, 安圭拉, 1\n" +
//                "AG, 安提瓜和巴布达, 1\n" +
//                "AU, 澳大利亚, 61\n" +
//                "AT, 奥地利, 43\n" +
//                "BB, 巴巴多斯, 1\n" +
//                "PG, 巴布亚新几内亚, 675\n" +
//                "BS, 巴哈马, 1\n" +
//                "PK, 巴基斯坦, 92\n" +
//                "PY, 巴拉圭, 595\n" +
//                "PS, 巴勒斯坦, 970\n" +
//                "BH, 巴林, 973\n" +
//                "PA, 巴拿马, 507\n" +
//                "BR, 巴西, 55\n" +
//                "BY, 白俄罗斯, 375\n" +
//                "BM, 百慕大, 1\n" +
//                "BG, 保加利亚, 359\n" +
//                "MP, 北马里亚纳群岛, 1\n" +
//                "BJ, 贝宁, 229\n" +
//                "BE, 比利时, 32\n" +
//                "PE, 秘鲁, 51\n" +
//                "IS, 冰岛, 354\n" +
//                "BW, 博茨瓦纳, 267\n" +
//                "PR, 波多黎各, 1\n" +
//                "BA, 波黑, 387\n" +
//                "PL, 波兰, 48\n" +
//                "BO, 玻利维亚, 591\n" +
//                "BZ, 伯利兹, 501\n" +
//                "BT, 不丹, 975\n" +
//                "BF, 布基纳法索, 226\n" +
//                "BI, 布隆迪, 257\n" +
//                "GQ, 赤道几内亚, 240\n" +
//                "DK, 丹麦, 45\n" +
//                "DE, 德国, 49\n" +
//                "TL, 东帝汶, 670\n" +
//                "TG, 多哥, 228\n" +
//                "DO, 多米尼加共和国, 1\n" +
//                "DM, 多米尼克, 1\n" +
//                "EC, 厄瓜多尔, 593\n" +
//                "ER, 厄立特里亚, 291\n" +
//                "RU, 俄罗斯, 7\n" +
//                "FR, 法国, 33\n" +
//                "FO, 法罗群岛, 298\n" +
//                "PF, 法属波利尼西亚, 689\n" +
//                "GF, 法属圭亚那, 594\n" +
//                "MF, 法属圣马丁, 590\n" +
//                "VA, 梵蒂冈, 379\n" +
//                "FJ, 斐济, 679\n" +
//                "PH, 菲律宾, 63\n" +
//                "FI, 芬兰, 358\n" +
//                "CV, 佛得角, 238\n" +
//                "FK, 福克兰群岛（马尔维纳斯群岛）, 500\n" +
//                "GM, 冈比亚, 220\n" +
//                "CG, 刚果, 242\n" +
//                "CD, 刚果民主共和国, 243\n" +
//                "GD, 格林纳达, 1\n" +
//                "GL, 格陵兰, 299\n" +
//                "GE, 格鲁吉亚, 995\n" +
//                "CO, 哥伦比亚, 57\n" +
//                "CR, 哥斯达黎加, 506\n" +
//                "CU, 古巴, 53\n" +
//                "GP, 瓜德罗普, 590\n" +
//                "GU, 关岛, 1\n" +
//                "GY, 圭亚那, 592\n" +
//                "KZ, 哈萨克斯坦, 7\n" +
//                "HT, 海地, 509\n" +
//                "KR, 韩国, 82\n" +
//                "NL, 荷兰, 31\n" +
//                "SX, 荷属圣马丁, 599\n" +
//                "ME, 黑山, 382\n" +
//                "HN, 洪都拉斯, 504\n" +
//                "DJ, 吉布提, 253\n" +
//                "KG, 吉尔吉斯斯坦, 996\n" +
//                "KI, 基里巴斯, 686\n" +
//                "GN, 几内亚, 224\n" +
//                "GW, 几内亚比绍, 245\n" +
//                "GH, 加纳, 233\n" +
//                "CA, 加拿大, 1\n" +
//                "GA, 加蓬, 241\n" +
//                "KH, 柬埔寨, 855\n" +
//                "CZ, 捷克, 420\n" +
//                "ZW, 津巴布韦, 263\n" +
//                "CM, 喀麦隆, 237\n" +
//                "KY, 开曼群岛, 1\n" +
//                "HR, 克罗地亚, 385\n" +
//                "KM, 科摩罗, 269\n" +
//                "XK, 科索沃, undefined\n" +
//                "CI, 科特迪瓦, 225\n" +
//                "KW, 科威特, 965\n" +
//                "KE, 肯尼亚, 254\n" +
//                "CK, 库克群岛, 682\n" +
//                "CW, 库拉索, 599\n" +
//                "LV, 拉脱维亚, 371\n" +
//                "LS, 莱索托, 266\n" +
//                "LA, 老挝, 856\n" +
//                "LB, 黎巴嫩, 961\n" +
//                "LR, 利比里亚, 231\n" +
//                "LY, 利比亚, 218\n" +
//                "LT, 立陶宛, 370\n" +
//                "LI, 列支敦士登, 423\n" +
//                "RE, 留尼汪, 262\n" +
//                "LU, 卢森堡, 352\n" +
//                "RW, 卢旺达, 250\n" +
//                "RO, 罗马尼亚, 40\n" +
//                "MG, 马达加斯加, 261\n" +
//                "MV, 马尔代夫, 960\n" +
//                "MT, 马耳他, 356\n" +
//                "MW, 马拉维, 265\n" +
//                "MY, 马来西亚, 60\n" +
//                "ML, 马里, 223\n" +
//                "MK, 马其顿, 389\n" +
//                "MH, 马绍尔群岛, 692\n" +
//                "MQ, 马提尼克, 596\n" +
//                "YT, 马约特, 262\n" +
//                "MU, 毛里求斯, 230\n" +
//                "MR, 毛里塔尼亚, 222\n" +
//                "US, 美国, 1\n" +
//                "AS, 美属萨摩亚, 1\n" +
//                "VI, 美属维尔京群岛, 1\n" +
//                "MN, 蒙古, 976\n" +
//                "BD, 孟加拉国, 880\n" +
//                "MS, 蒙塞拉特岛, 1\n" +
//                "FM, 密克罗尼西亚联邦, 691\n" +
//                "MM, 缅甸, 95\n" +
//                "MD, 摩尔多瓦, 373\n" +
//                "MA, 摩洛哥, 212\n" +
//                "MC, 摩纳哥, 377\n" +
//                "MZ, 莫桑比克, 258\n" +
//                "MX, 墨西哥, 52\n" +
//                "NA, 纳米比亚, 264\n" +
//                "ZA, 南非, 27\n" +
//                "SS, 南苏丹, 211\n" +
//                "NR, 瑙鲁, 674\n" +
//                "NP, 尼泊尔, 977\n" +
//                "NI, 尼加拉瓜, 505\n" +
//                "NE, 尼日尔, 227\n" +
//                "NG, 尼日利亚, 234\n" +
//                "NU, 纽埃, 683\n" +
//                "NO, 挪威, 47\n" +
//                "PW, 帕劳, 680\n" +
//                "PN, 皮特凯恩群岛, 870\n" +
//                "PT, 葡萄牙, 351\n" +
//                "QA, 卡塔尔, 974\n" +
//                "JP, 日本, 81\n" +
//                "SE, 瑞典, 46\n" +
//                "CH, 瑞士, 41\n" +
//                "SV, 萨尔瓦多, 503\n" +
//                "WS, 萨摩亚, 685\n" +
//                "RS, 塞尔维亚, 381\n" +
//                "SL, 塞拉利昂, 232\n" +
//                "SN, 塞内加尔, 221\n" +
//                "CY, 塞浦路斯, 357\n" +
//                "SC, 塞舌尔, 248\n" +
//                "SA, 沙特阿拉伯, 966\n" +
//                "BL, 圣巴泰勒米岛, 590\n" +
//                "ST, 圣多美和普林西比, 239\n" +
//                "KN, 圣基茨和尼维斯, 1\n" +
//                "LC, 圣卢西亚, 1\n" +
//                "SM, 圣马力诺, 378\n" +
//                "PM, 圣皮埃尔和密克隆群岛, 508\n" +
//                "VC, 圣文森特和格林纳丁斯, 1\n" +
//                "BQ, 圣尤斯特歇斯, 599\n" +
//                "LK, 斯里兰卡, 94\n" +
//                "SK, 斯洛伐克, 421\n" +
//                "SI, 斯洛文尼亚, 386\n" +
//                "SZ, 斯威士兰, 268\n" +
//                "SD, 苏丹, 249\n" +
//                "SR, 苏里南, 597\n" +
//                "SB, 所罗门群岛, 677\n" +
//                "SO, 索马里, 252\n" +
//                "TJ, 塔吉克斯坦, 992\n" +
//                "TH, 泰国, 66\n" +
//                "TZ, 坦桑尼亚, 255\n" +
//                "TO, 汤加, 676\n" +
//                "TC, 特克斯和凯科斯群岛, 1\n" +
//                "TT, 特立尼达和多巴哥, 1\n" +
//                "SH, 特里斯坦-达库尼亚群岛, 290\n" +
//                "TR, 土耳其, 90\n" +
//                "TM, 土库曼斯坦, 993\n" +
//                "TN, 突尼斯, 216\n" +
//                "TV, 图瓦卢, 688\n" +
//                "TK, 托克劳, 690\n" +
//                "WF, 瓦利斯群岛和富图纳群岛, 681\n" +
//                "VU, 瓦努阿图, 678\n" +
//                "GT, 危地马拉, 502\n" +
//                "VE, 委内瑞拉, 58\n" +
//                "BN, 文莱, 673\n" +
//                "UG, 乌干达, 256\n" +
//                "UA, 乌克兰, 380\n" +
//                "UY, 乌拉圭, 598\n" +
//                "UZ, 乌兹别克斯坦, 998\n" +
//                "ES, 西班牙, 34\n" +
//                "GR, 希腊, 30\n" +
//                "EH, 西撒哈拉, 212\n" +
//                "SG, 新加坡, 65\n" +
//                "NC, 新喀里多尼亚, 687\n" +
//                "NZ, 新西兰, 64\n" +
//                "HU, 匈牙利, 36\n" +
//                "SY, 叙利亚, 963\n" +
//                "JM, 牙买加, 1\n" +
//                "AM, 亚美尼亚, 374\n" +
//                "YE, 也门, 967\n" +
//                "IT, 意大利, 39\n" +
//                "IQ, 伊拉克, 964\n" +
//                "IR, 伊朗, 98\n" +
//                "IL, 以色列, 972\n" +
//                "IN, 印度, 91\n" +
//                "ID, 印度尼西亚, 62\n" +
//                "UK, 英国, undefined\n" +
//                "VG, 英属维尔京群岛, 1\n" +
//                "IO, 英属印度洋领地, 246\n" +
//                "JO, 约旦, 962\n" +
//                "VN, 越南, 84\n" +
//                "ZM, 赞比亚, 260\n" +
//                "TD, 乍得, 235\n" +
//                "KP, 朝鲜, 850\n" +
//                "GI, 直布罗陀, 350\n" +
//                "CL, 智利, 56\n" +
//                "CF, 中非共和国, 236\n" +
//                "CN, 中国, 86";
//        String[] str = string.split("\n");
//            ArrayList<Country> list = new ArrayList<>();
//            for (String s : str) {
//                    String[] objArray = s.split(",");
//
//                String p = CharacterParser.getInstance().getSelling(objArray[1]);
//                    list.add(new Country(objArray[2].trim(),objArray[1].trim(),objArray[0],p.trim()));
//            }
//
//        sortConversationByLastChatTime(list);
//        Collections.reverse(list);
//            System.out.println( JSON.toJSON(list).toString());
//    }
//        public static class Country{
//                public String dialCode;
//                public String countryName;
//                public String countryCode;
//                public String header;
//                public Country( String dialCode, String countryName,String countryCode,String header){
//                        this.dialCode = dialCode;
//                        this.countryCode = countryCode;
//                        this.countryName = countryName;
//                        this.header = header;
//                }
//        }
//
//    private static void sortConversationByLastChatTime(List<Country> conversationList) {
//        Collections.sort(conversationList, new Comparator<Country>() {
//            @Override
//            public int compare(final Country con1, final Country con2) {
//
//                long LastTime2 = con2.header.charAt(0);
//                long LastTime1 = con1.header.charAt(0);
//                if (LastTime1 == 0 || LastTime2 == 0) {
//                    return -1;
//                }
//                if (LastTime2 == LastTime1) {
//                    return 0;
//                } else if (LastTime2 > LastTime1) {
//                    return 1;
//                } else {
//                    return -1;
//                }
//            }
//
//        });
//    }
//
//
//    public static class CharacterParser {
//        private static int[] pyvalue = new int[] {-20319, -20317, -20304, -20295, -20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036, -20032,
//                -20026, -20002, -19990, -19986, -19982, -19976, -19805, -19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741, -19739, -19728,
//                -19725, -19715, -19540, -19531, -19525, -19515, -19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275, -19270, -19263, -19261,
//                -19249, -19243, -19242, -19238, -19235, -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006, -19003, -18996, -18977, -18961,
//                -18952, -18783, -18774, -18773, -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697, -18696, -18526, -18518, -18501, -18490,
//                -18478, -18463, -18448, -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201, -18184, -18183, -18181, -18012, -17997, -17988,
//                -17970, -17964, -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752, -17733, -17730, -17721, -17703, -17701, -17697, -17692,
//                -17683, -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427, -17417, -17202, -17185, -16983, -16970, -16942, -16915, -16733,
//                -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470, -16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423, -16419,
//                -16412, -16407, -16403, -16401, -16393, -16220, -16216, -16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158, -16155, -15959,
//                -15958, -15944, -15933, -15920, -15915, -15903, -15889, -15878, -15707, -15701, -15681, -15667, -15661, -15659, -15652, -15640, -15631,
//                -15625, -15454, -15448, -15436, -15435, -15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369, -15363, -15362, -15183, -15180,
//                -15165, -15158, -15153, -15150, -15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121, -15119, -15117, -15110, -15109, -14941,
//                -14937, -14933, -14930, -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902, -14894, -14889, -14882, -14873, -14871, -14857,
//                -14678, -14674, -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429, -14407, -14399, -14384, -14379, -14368, -14355, -14353,
//                -14345, -14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135, -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094,
//                -14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907, -13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847, -13831,
//                -13658, -13611, -13601, -13406, -13404, -13400, -13398, -13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343, -13340, -13329,
//                -13326, -13318, -13147, -13138, -13120, -13107, -13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888, -12875, -12871, -12860,
//                -12858, -12852, -12849, -12838, -12831, -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556, -12359, -12346, -12320, -12300,
//                -12120, -12099, -12089, -12074, -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798, -11781, -11604, -11589, -11536, -11358,
//                -11340, -11339, -11324, -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041, -11038, -11024, -11020, -11019, -11018, -11014,
//                -10838, -10832, -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533, -10519, -10331, -10329, -10328, -10322, -10315, -10309,
//                -10307, -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254};
//        public static String[] pystr = new String[] {"a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian",
//                "biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai", "chan", "chang", "chao", "che",
//                "chen", "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu", "cuan",
//                "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die", "ding", "diu", "dong", "dou", "du",
//                "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu", "ga", "gai", "gan", "gang",
//                "gao", "ge", "gei", "gen", "geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang",
//                "hao", "he", "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo", "ji", "jia", "jian",
//                "jiang", "jiao", "jie", "jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan", "kang", "kao", "ke", "ken",
//                "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan", "lang", "lao", "le", "lei", "leng",
//                "li", "lia", "lian", "liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "lv", "luan", "lue", "lun", "luo", "ma", "mai",
//                "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai",
//                "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan",
//                "nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pu",
//                "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao", "re",
//                "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen", "seng", "sha",
//                "shai", "shan", "shang", "shao", "she", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang", "shui", "shun",
//                "shuo", "si", "song", "sou", "su", "suan", "sui", "sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao",
//                "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu", "xi",
//                "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya", "yan", "yang", "yao", "ye", "yi",
//                "yin", "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang", "zao", "ze", "zei", "zen", "zeng", "zha",
//                "zhai", "zhan", "zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui",
//                "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo"};
//        private StringBuilder buffer;
//        private String resource;
//        private static CharacterParser characterParser = new CharacterParser();
//
//        public static CharacterParser getInstance() {
//            return characterParser;
//        }
//
//        public String getResource() {
//            return resource;
//        }
//
//        public void setResource(String resource) {
//            this.resource = resource;
//        }
//
//        /** * 汉字转成ASCII码 * * @param chs * @return */
//        private int getChsAscii(String chs) {
//            int asc = 0;
//            try {
//                byte[] bytes = chs.getBytes("gb2312");
//                if (bytes == null || bytes.length > 2 || bytes.length <= 0) {
//                    throw new RuntimeException("illegal resource string");
//                }
//                if (bytes.length == 1) {
//                    asc = bytes[0];
//                }
//                if (bytes.length == 2) {
//                    int hightByte = 256 + bytes[0];
//                    int lowByte = 256 + bytes[1];
//                    asc = (256 * hightByte + lowByte) - 256 * 256;
//                }
//            } catch (Exception e) {
//                System.out.println("ERROR:ChineseSpelling.class-getChsAscii(String chs)" + e);
//            }
//            return asc;
//        }
//
//        /** * 单字解析 * * @param str * @return */
//        public String convert(String str) {
//            String result = null;
//            int ascii = getChsAscii(str);
//            if (ascii > 0 && ascii < 160) {
//                result = String.valueOf((char) ascii);
//            } else {
//                for (int i = (pyvalue.length - 1); i >= 0; i--) {
//                    if (pyvalue[i] <= ascii) {
//                        result = pystr[i];
//                        break;
//                    }
//                }
//            }
//            return result;
//        }
//
//        /** * 词组解析 * * @param chs * @return */
//        public String getSelling(String chs) {
//            String key, value;
//            buffer = new StringBuilder();
//            for (int i = 0; i < chs.length(); i++) {
//                key = chs.substring(i, i + 1);
//                if (key.getBytes().length >= 2) {
//                    value = (String) convert(key);
//                    if (value == null) {
//                        value = "unknown";
//                    }
//                } else {
//                    value = key;
//                }
//                buffer.append(value);
//            }
//            return buffer.toString();
//        }
//
//        public String getSpelling() {
//            return this.getSelling(this.getResource());
//        }
//
//    }
//}
