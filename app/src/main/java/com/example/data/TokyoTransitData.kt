package com.example.data

import com.example.model.*
import com.example.ui.theme.*

object TokyoTransitData {

    // --- TRANSIT LINES ---
    val lineYamanote = TransitLine(
        id = "line_jy",
        code = "JY",
        nameEn = "JR Yamanote Line",
        nameJa = "山手線",
        operator = RailOperator.JR_EAST,
        color = JrYamanoteGreen,
        isCircular = true
    )

    val lineChuoRapid = TransitLine(
        id = "line_jc",
        code = "JC",
        nameEn = "JR Chuo Line (Rapid)",
        nameJa = "中央線快速",
        operator = RailOperator.JR_EAST,
        color = JrChuoOrange
    )

    val lineSobuLocal = TransitLine(
        id = "line_jb",
        code = "JB",
        nameEn = "JR Chuo-Sobu (Local)",
        nameJa = "総武線各駅停車",
        operator = RailOperator.JR_EAST,
        color = JrSobuYellow
    )

    val lineKeihinTohoku = TransitLine(
        id = "line_jk",
        code = "JK",
        nameEn = "JR Keihin-Tohoku Line",
        nameJa = "京浜東北線",
        operator = RailOperator.JR_EAST,
        color = JrKeihinBlue
    )

    val lineGinza = TransitLine(
        id = "line_g",
        code = "G",
        nameEn = "Tokyo Metro Ginza Line",
        nameJa = "銀座線",
        operator = RailOperator.TOKYO_METRO,
        color = MetroGinzaOrange
    )

    val lineMarunouchi = TransitLine(
        id = "line_m",
        code = "M",
        nameEn = "Tokyo Metro Marunouchi Line",
        nameJa = "丸ノ内線",
        operator = RailOperator.TOKYO_METRO,
        color = MetroMarunouchiRed
    )

    val lineHibiya = TransitLine(
        id = "line_h",
        code = "H",
        nameEn = "Tokyo Metro Hibiya Line",
        nameJa = "日比谷線",
        operator = RailOperator.TOKYO_METRO,
        color = MetroHibiyaSilver
    )

    val lineTozai = TransitLine(
        id = "line_t",
        code = "T",
        nameEn = "Tokyo Metro Tozai Line",
        nameJa = "東西線",
        operator = RailOperator.TOKYO_METRO,
        color = MetroTozaiBlue
    )

    val lineChiyoda = TransitLine(
        id = "line_c",
        code = "C",
        nameEn = "Tokyo Metro Chiyoda Line",
        nameJa = "千代田線",
        operator = RailOperator.TOKYO_METRO,
        color = MetroChiyodaGreen
    )

    val lineHanzomon = TransitLine(
        id = "line_z",
        code = "Z",
        nameEn = "Tokyo Metro Hanzomon Line",
        nameJa = "半蔵門線",
        operator = RailOperator.TOKYO_METRO,
        color = MetroHanzomonPurple
    )

    val lineFukutoshin = TransitLine(
        id = "line_f",
        code = "F",
        nameEn = "Tokyo Metro Fukutoshin Line",
        nameJa = "副都心線",
        operator = RailOperator.TOKYO_METRO,
        color = MetroFukutoshinBrown
    )

    val lineOedo = TransitLine(
        id = "line_e",
        code = "E",
        nameEn = "Toei Oedo Line",
        nameJa = "都営大江戸線",
        operator = RailOperator.TOEI_SUBWAY,
        color = ToeiOedoMagenta
    )

    val lineToeiShinjuku = TransitLine(
        id = "line_s",
        code = "S",
        nameEn = "Toei Shinjuku Line",
        nameJa = "都営新宿線",
        operator = RailOperator.TOEI_SUBWAY,
        color = ToeiShinjukuGreen
    )

    val lineToeiAsakusa = TransitLine(
        id = "line_a",
        code = "A",
        nameEn = "Toei Asakusa Line",
        nameJa = "都営浅草線",
        operator = RailOperator.TOEI_SUBWAY,
        color = ToeiAsakusaRose
    )

    val lineOdakyu = TransitLine(
        id = "line_oh",
        code = "OH",
        nameEn = "Odakyu Odawara Line",
        nameJa = "小田急小田原線",
        operator = RailOperator.ODAKYU,
        color = OdakyuBlue
    )

    val lineKeio = TransitLine(
        id = "line_ko",
        code = "KO",
        nameEn = "Keio Line",
        nameJa = "京王線",
        operator = RailOperator.KEIO,
        color = KeioPink
    )

    val lineTokyuToyoko = TransitLine(
        id = "line_ty",
        code = "TY",
        nameEn = "Tokyu Toyoko Line",
        nameJa = "東急東横線",
        operator = RailOperator.TOKYU,
        color = TokyuRed
    )

    val allLines = listOf(
        lineYamanote, lineChuoRapid, lineSobuLocal, lineKeihinTohoku,
        lineGinza, lineMarunouchi, lineHibiya, lineTozai, lineChiyoda,
        lineHanzomon, lineFukutoshin, lineOedo, lineToeiShinjuku,
        lineToeiAsakusa, lineOdakyu, lineKeio, lineTokyuToyoko
    )

    // --- STATIONS ---
    val stationShinjuku = Station(
        id = "sta_shinjuku",
        nameEn = "Shinjuku",
        nameJa = "新宿",
        kana = "しんじゅく",
        latitude = 35.6896,
        longitude = 139.7006,
        lines = listOf(lineYamanote, lineChuoRapid, lineSobuLocal, lineMarunouchi, lineToeiShinjuku, lineOedo, lineOdakyu, lineKeio),
        operators = setOf(RailOperator.JR_EAST, RailOperator.TOKYO_METRO, RailOperator.TOEI_SUBWAY, RailOperator.ODAKYU, RailOperator.KEIO),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Over 1,200 lockers across South, East & West Exits (Suica payment supported)", "南口・東口・西口に計1,200個以上設置（Suica対応）", true),
            StationAmenity(AmenityType.ELEVATOR, "Full barrier-free access connecting all JR platforms and Metro concourse", "全JRホーム・地下鉄コンコースにエレベーター完備", true),
            StationAmenity(AmenityType.FREE_WIFI, "JR-EAST FREE Wi-Fi & Tokyo Metro Free Wi-Fi across all concourses", "JR東日本・メトロ公式無料Wi-Fi完備", true),
            StationAmenity(AmenityType.TICKET_OFFICE, "Midori no Madoguchi at South & East Exits (Japan Rail Pass exchange)", "みどりの窓口（南口・東口）JRパス引換対応", true),
            StationAmenity(AmenityType.RESTROOM, "Barrier-free multi-purpose accessible restrooms at all ticket gates", "全改札内外に多機能トイレ・オストメイト対応あり", true),
            StationAmenity(AmenityType.CONVENIENCE, "NewDays convenience stores & Ekiben dining markets at central passageway", "駅ナカNewDays・駅弁屋頂など多数", true)
        ),
        platforms = listOf(
            PlatformInfo("1-2", "JA", "JR Saikyo Line", "Shibuya, Shin-Kiba, Yokohama", "渋谷・新木場・横浜方面"),
            PlatformInfo("7-8", "JC", "JR Chuo Rapid", "Nakano, Tachikawa, Takao", "中野・立川・高尾方面"),
            PlatformInfo("13", "JB", "JR Sobu Local", "Ochanomizu, Akihabara, Chiba", "御茶ノ水・秋葉原・千葉方面"),
            PlatformInfo("14", "JY", "JR Yamanote (Clockwise)", "Shibuya, Shinagawa", "渋谷・品川方面（内回り）"),
            PlatformInfo("15", "JY", "JR Yamanote (Counter-clockwise)", "Ikebukuro, Ueno", "池袋・上野方面（外回り）"),
            PlatformInfo("M1-2", "M", "Tokyo Metro Marunouchi", "Ginza, Tokyo, Ikebukuro", "銀座・東京・池袋方面"),
            PlatformInfo("OH1-4", "OH", "Odakyu Odawara Line", "Machida, Odawara, Hakone", "町田・小田原・箱根湯本方面")
        ),
        exits = listOf(
            ExitInfo("South Exit", "南口", "Direct access to Koshu Kaido, Lumine 2, and Busta Shinjuku", listOf("Lumine 2", "Busta Shinjuku", "Flags")),
            ExitInfo("East Exit", "東口", "Vibrant entertainment district, Studio Alta, and Kabukicho", listOf("Kabukicho", "Studio ALTA", "Isetan")),
            ExitInfo("West Exit", "西口", "Skyscrapers and Tokyo Metropolitan Government Building", listOf("Tokyo Gov Building", "Keio Dept Store", "Odakyu Dept Store")),
            ExitInfo("New South Exit", "新南口", "High-speed highway bus terminal and Takashimaya Times Square", listOf("Busta Bus Terminal", "Takashimaya", "NEWoMan"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("JY", "Yamanote Outer", "Ikebukuro / Ueno", "池袋・上野方面", 2, "Plat 15", "On Time"),
            DepartureInfo("JC", "Chuo Rapid", "Tokyo Terminal", "東京行", 3, "Plat 8", "Rapid", true),
            DepartureInfo("JY", "Yamanote Inner", "Shibuya / Shinagawa", "渋谷・品川方面", 4, "Plat 14", "On Time"),
            DepartureInfo("M", "Marunouchi", "Tokyo / Ikebukuro", "東京・池袋方面", 5, "Plat M2", "On Time"),
            DepartureInfo("OH", "Odakyu Express", "Hakone-Yumoto Romancecar", "箱根湯本行 特急", 12, "Plat OH1", "Reserved", true)
        ),
        addressJa = "東京都新宿区新宿三丁目38-1",
        isMajorHub = true
    )

    val stationShibuya = Station(
        id = "sta_shibuya",
        nameEn = "Shibuya",
        nameJa = "渋谷",
        kana = "しぶや",
        latitude = 35.6580,
        longitude = 139.7016,
        lines = listOf(lineYamanote, lineGinza, lineHanzomon, lineFukutoshin, lineTokyuToyoko, lineKeio),
        operators = setOf(RailOperator.JR_EAST, RailOperator.TOKYO_METRO, RailOperator.TOKYU, RailOperator.KEIO),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "800+ lockers inside Shibuya Scramble Square & Underground B2 passage", "スクランブルスクエア地下・地下2階通路に800台以上", true),
            StationAmenity(AmenityType.ELEVATOR, "Direct elevator access from Hachiko plaza to Ginza Line & Tokyu B5", "ハチ公前広場・銀座線新駅舎・東急B5に直通エレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "Shibuya City Free Wi-Fi & JR-EAST Free Wi-Fi throughout", "SHIBUYA_Wi-Fi & JR公式Wi-Fi完備", true),
            StationAmenity(AmenityType.TICKET_OFFICE, "JR Ticket Office next to Hachiko Ticket Gate (JR Pass exchange)", "ハチ公改札横JRみどりの窓口", true),
            StationAmenity(AmenityType.RESTROOM, "Modern accessible restrooms at Hikarie Gate & Scramble Gate", "ヒカリエ改札・スクランブル改札に最新多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Tokyu Foodshow, Shibuya Scramble Gourmet, and Lawson/NewDays", "東急フードショー・駅ナカグルメ・売店充実", true)
        ),
        platforms = listOf(
            PlatformInfo("1", "JY", "JR Yamanote (Clockwise)", "Shinagawa, Tokyo", "品川・東京方面（内回り）"),
            PlatformInfo("2", "JY", "JR Yamanote (Counter-clockwise)", "Shinjuku, Ikebukuro", "新宿・池袋方面（外回り）"),
            PlatformInfo("G1-2", "G", "Tokyo Metro Ginza", "Omotesando, Ginza, Asakusa", "表参道・銀座・浅草方面"),
            PlatformInfo("TY1-2", "TY", "Tokyu Toyoko Line", "Jiyugaoka, Yokohama, Motomachi", "自由が丘・横浜・元町中華街方面"),
            PlatformInfo("Z1-2", "Z", "Tokyo Metro Hanzomon", "Otemachi, Oshiage (Skytree)", "大手町・押上（スカイツリー前）方面")
        ),
        exits = listOf(
            ExitInfo("Hachiko Exit", "ハチ公口", "The world-famous Shibuya Scramble Crossing & Hachiko Statue", listOf("Hachiko Statue", "Scramble Crossing", "SHIBUYA109")),
            ExitInfo("Miyamasuzaka Exit", "宮益坂口", "Towards Miyashita Park and Omotesando avenue", listOf("Miyashita Park", "Hikarie", "Miyamasuzaka")),
            ExitInfo("Scramble Square Exit", "スクランブルスクエア口", "Direct elevator to SHIBUYA SKY observation deck", listOf("SHIBUYA SKY", "Shibuya Stream")),
            ExitInfo("West Exit", "西口", "Bus terminal to Roppongi, Ebisu, and Haneda Airport Limousine", listOf("Bus Terminal", "Mark City"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("JY", "Yamanote Inner", "Shinagawa / Tokyo", "品川・東京方面", 1, "Plat 1", "On Time"),
            DepartureInfo("G", "Ginza Line", "Asakusa Terminal", "浅草行", 3, "Plat G1", "On Time"),
            DepartureInfo("TY", "Toyoko Exp", "Yokohama / Motomachi", "横浜・元町中華街行", 4, "Plat TY2", "Express", true),
            DepartureInfo("JY", "Yamanote Outer", "Shinjuku / Ikebukuro", "新宿・池袋方面", 5, "Plat 2", "On Time")
        ),
        addressJa = "東京都渋谷区道玄坂一丁目1-1",
        isMajorHub = true
    )

    val stationTokyo = Station(
        id = "sta_tokyo",
        nameEn = "Tokyo Station",
        nameJa = "東京",
        kana = "とうきょう",
        latitude = 35.6812,
        longitude = 139.7671,
        lines = listOf(lineYamanote, lineChuoRapid, lineKeihinTohoku, lineSobuLocal, lineMarunouchi),
        operators = setOf(RailOperator.JR_EAST, RailOperator.TOKYO_METRO, RailOperator.SHINKANSEN),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "3,000+ lockers across GranSta Tokyo, Yaesu Underground & Marunouchi", "グランスタ東京・八重洲地下街・丸の内口に3000個超", true),
            StationAmenity(AmenityType.ELEVATOR, "Complete barrier-free network connecting all Shinkansen & local lines", "全新幹線ホーム・在来線・地下鉄にエレベーター完備", true),
            StationAmenity(AmenityType.FREE_WIFI, "JR-EAST Free Wi-Fi & Tokyo Station Central Wi-Fi high-speed", "JR東日本・東京駅無料Wi-Fi高速接続", true),
            StationAmenity(AmenityType.TICKET_OFFICE, "JR East Travel Service Center (Foreign tourist pass exchange & English desk)", "JR東日本訪日旅行センター（英語・多言語対応）", true),
            StationAmenity(AmenityType.RESTROOM, "Universal accessible restrooms with baby nursing rooms at concourses", "授乳室併設多機能トイレ・各改札内完備", true),
            StationAmenity(AmenityType.CONVENIENCE, "GranSta Tokyo, Daimaru Yaesu, Tokyo Ramen Street, Ekiben Matsuri", "グランスタ・大丸・東京ラーメンストリート・駅弁屋祭", true)
        ),
        platforms = listOf(
            PlatformInfo("1-2", "JC", "JR Chuo Rapid", "Ochanomizu, Shinjuku, Takao", "御茶ノ水・新宿・高尾方面"),
            PlatformInfo("4", "JY", "JR Yamanote (Clockwise)", "Shinagawa, Shibuya", "品川・渋谷方面（内回り）"),
            PlatformInfo("5", "JY", "JR Yamanote (Counter-clockwise)", "Ueno, Ikebukuro", "上野・池袋方面（外回り）"),
            PlatformInfo("14-19", "Tokaido", "Tokaido Shinkansen", "Nagoya, Kyoto, Shin-Osaka", "名古屋・京都・新大阪方面"),
            PlatformInfo("20-23", "Tohoku", "Tohoku/Hokkaido Shinkansen", "Sendai, Morioka, Shin-Aomori", "仙台・盛岡・新青森方面"),
            PlatformInfo("M1-2", "M", "Tokyo Metro Marunouchi", "Ginza, Shinjuku, Ogikubo", "銀座・新宿・荻窪方面")
        ),
        exits = listOf(
            ExitInfo("Marunouchi Central", "丸の内中央口", "Historic 1914 red brick station building facing Imperial Palace", listOf("Imperial Palace", "Marunouchi Building", "Tokyo Station Hotel")),
            ExitInfo("Yaesu Central Exit", "八重洲中央口", "Highway bus terminal, Tokyo Station City, and Shinkansen gates", listOf("Shinkansen Gates", "Tokyo Midtown Yaesu", "Yaesu Underground")),
            ExitInfo("Nihombashi Exit", "日本橋口", "Express bus arrivals and Nihombashi business district", listOf("Nihombashi River", "Tokyo Torch Tower")),
            ExitInfo("Keiyo Underground", "京葉地下改札", "Direct connection to Keiyo Line for Tokyo Disney Resort", listOf("Tokyo Disney Resort trains", "Tokyo International Forum"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("Shinkansen", "Nozomi 45", "Shin-Osaka / Hakata", "新大阪・博多行", 3, "Plat 16", "Express", true),
            DepartureInfo("JY", "Yamanote Inner", "Shinagawa / Shibuya", "品川・渋谷方面", 2, "Plat 4", "On Time"),
            DepartureInfo("JC", "Chuo Rapid", "Shinjuku / Takao", "新宿・高尾行", 4, "Plat 2", "Rapid", true),
            DepartureInfo("M", "Marunouchi", "Ginza / Shinjuku", "銀座・新宿方面", 5, "Plat M1", "On Time")
        ),
        addressJa = "東京都千代田区丸の内一丁目9-1",
        isMajorHub = true
    )

    val stationAkihabara = Station(
        id = "sta_akihabara",
        nameEn = "Akihabara",
        nameJa = "秋葉原",
        kana = "あきはばら",
        latitude = 35.6983,
        longitude = 139.7731,
        lines = listOf(lineYamanote, lineKeihinTohoku, lineSobuLocal, lineHibiya),
        operators = setOf(RailOperator.JR_EAST, RailOperator.TOKYO_METRO, RailOperator.TSUKUBA),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Electric Town Exit & Central Gate lockers (Extra large for PC/anime merch)", "電気街口・中央改札に大型ロッカー多数（大型荷物対応）", true),
            StationAmenity(AmenityType.ELEVATOR, "Elevators connecting Sobu cross-over upper platforms to Yamanote lower level", "総武線上層ホームと山手線地上ホームを結ぶエレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "JR-EAST Free Wi-Fi & Akihabara Free Wi-Fi spot", "JR東日本・秋葉原フリーWi-Fi", true),
            StationAmenity(AmenityType.TICKET_OFFICE, "Midori no Madoguchi at Central Gate", "中央改札みどりの窓口", true),
            StationAmenity(AmenityType.RESTROOM, "Modern restrooms at Central Gate and Electric Town Gate", "中央改札・電気街改札内多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Yodobashi Akiba direct passage, atré Akihabara, NewDays", "ヨドバシAkiba直通・アトレ秋葉原・売店", true)
        ),
        platforms = listOf(
            PlatformInfo("1", "JY", "JR Keihin-Tohoku", "Ueno, Omiya", "上野・大宮方面"),
            PlatformInfo("2", "JY", "JR Yamanote (Counter-clockwise)", "Ueno, Ikebukuro", "上野・池袋方面（外回り）"),
            PlatformInfo("3", "JY", "JR Yamanote (Clockwise)", "Tokyo, Shinagawa", "東京・品川方面（内回り）"),
            PlatformInfo("5-6", "JB", "JR Sobu Local", "Shinjuku, Nakano / Chiba", "新宿・中野／千葉方面（高架上層）"),
            PlatformInfo("H1-2", "H", "Tokyo Metro Hibiya", "Ueno, Kitasenju / Ginza, Roppongi", "上野・北千住／銀座・六本木方面")
        ),
        exits = listOf(
            ExitInfo("Electric Town Exit", "電気街口", "Heart of anime, electronics, manga, and maid cafes", listOf("Chuo-dori", "Radio Kaikan", "Animate")),
            ExitInfo("Central Gate", "中央改札", "Direct access to Tsukuba Express and Yodobashi Camera", listOf("Yodobashi Akiba", "atré 1", "Plaza")),
            ExitInfo("Showa-dori Gate", "昭和通り口", "Convenient transfer to Tokyo Metro Hibiya Line", listOf("Tokyo Metro Hibiya", "Chiyoda Ward Office"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("JY", "Yamanote Outer", "Ueno / Ikebukuro", "上野・池袋方面", 1, "Plat 2", "On Time"),
            DepartureInfo("JB", "Sobu Local", "Ochanomizu / Shinjuku", "御茶ノ水・新宿方面", 3, "Plat 5", "On Time"),
            DepartureInfo("H", "Hibiya Line", "Ginza / Roppongi", "銀座・六本木方面", 4, "Plat H2", "On Time")
        ),
        addressJa = "東京都千代田区外神田一丁目17-6",
        isMajorHub = true
    )

    val stationUeno = Station(
        id = "sta_ueno",
        nameEn = "Ueno",
        nameJa = "上野",
        kana = "うえの",
        latitude = 35.7141,
        longitude = 139.7774,
        lines = listOf(lineYamanote, lineKeihinTohoku, lineGinza, lineHibiya),
        operators = setOf(RailOperator.JR_EAST, RailOperator.TOKYO_METRO, RailOperator.KEISEI, RailOperator.SHINKANSEN),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Huge locker facility at Park Exit and Central Concourse", "公園口・中央改札コンコースに大型ロッカー多数", true),
            StationAmenity(AmenityType.ELEVATOR, "Direct elevator bridge to Ueno Park, Tokyo National Museum, Zoo", "上野公園・上野動物園・国立博物館直通エレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "JR-EAST Free Wi-Fi & Ueno Cultural Park Wi-Fi", "JR公式・上野文化パークWi-Fi完備", true),
            StationAmenity(AmenityType.TICKET_OFFICE, "JR East Travel Service Center & Midori no Madoguchi", "JR東日本訪日旅行センター・みどりの窓口", true),
            StationAmenity(AmenityType.RESTROOM, "Universal accessible restrooms at Park Exit & Hirokoji", "公園口・広小路口多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "atré Ueno, Ekiben Ueno, Ecute Ueno shopping concourse", "エキュート上野・アトレ上野・駅弁多数", true)
        ),
        platforms = listOf(
            PlatformInfo("1-2", "JY", "JR Yamanote (Clockwise)", "Tokyo, Shinagawa", "東京・品川方面（内回り）"),
            PlatformInfo("3", "JY", "JR Yamanote (Counter-clockwise)", "Tabata, Ikebukuro", "田端・池袋方面（外回り）"),
            PlatformInfo("G1-2", "G", "Tokyo Metro Ginza", "Asakusa / Ginza, Shibuya", "浅草／銀座・渋谷方面"),
            PlatformInfo("H1-2", "H", "Tokyo Metro Hibiya", "Akihabara, Roppongi", "秋葉原・六本木方面")
        ),
        exits = listOf(
            ExitInfo("Park Exit", "公園口", "Direct pedestrian access into Ueno Onshi Park, Zoo, and Museums", listOf("Ueno Park", "Tokyo National Museum", "Ueno Zoo")),
            ExitInfo("Central Gate", "中央改札", "Main concourse connecting to Grand Dining and atré Ueno", listOf("atré Ueno", "Ameyoko Shopping Street")),
            ExitInfo("Shinobazu Exit", "不忍口", "Direct walking route to Ameyoko market and Shinobazu Pond", listOf("Ameyoko Market", "Shinobazu Pond"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("G", "Ginza Line", "Asakusa Terminal", "浅草行", 2, "Plat G1", "On Time"),
            DepartureInfo("JY", "Yamanote Inner", "Tokyo / Shinagawa", "東京・品川方面", 3, "Plat 2", "On Time"),
            DepartureInfo("G", "Ginza Line", "Ginza / Shibuya", "銀座・渋谷行", 4, "Plat G2", "On Time")
        ),
        addressJa = "東京都台東区上野七丁目1-1",
        isMajorHub = true
    )

    val stationGinza = Station(
        id = "sta_ginza",
        nameEn = "Ginza",
        nameJa = "銀座",
        kana = "ぎんざ",
        latitude = 35.6719,
        longitude = 139.7640,
        lines = listOf(lineGinza, lineMarunouchi, lineHibiya),
        operators = setOf(RailOperator.TOKYO_METRO),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Underground passageway lockers at Ginza 4-Chome & A-Exits", "銀座四丁目地下通路・A出口ロッカー", true),
            StationAmenity(AmenityType.ELEVATOR, "Elevators to Ginza Crossing, Ginza Six, and Wako clock tower", "銀座4丁目交差点・GINZA SIX直通エレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "Tokyo Metro Free Wi-Fi throughout all platforms", "東京メトロフリーWi-Fi全域対応", true),
            StationAmenity(AmenityType.RESTROOM, "Clean modern multi-purpose restrooms at Ginza Central concourse", "銀座中央コンコース多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Echika fit Ginza underground boutique bakery and cafe", "エチカフィット銀座・カフェ・ベーカリー", true)
        ),
        platforms = listOf(
            PlatformInfo("1", "G", "Ginza Line", "Akasaka-mitsuke, Shibuya", "赤坂見附・渋谷方面"),
            PlatformInfo("2", "G", "Ginza Line", "Nihombashi, Asakusa", "日本橋・浅草方面"),
            PlatformInfo("3", "M", "Marunouchi Line", "Tokyo, Ikebukuro", "東京・池袋方面"),
            PlatformInfo("4", "M", "Marunouchi Line", "Yotsuya, Shinjuku, Ogikubo", "四ツ谷・新宿・荻窪方面"),
            PlatformInfo("5-6", "H", "Hibiya Line", "Roppongi, Naka-Meguro / Ueno", "六本木・中目黒／上野方面")
        ),
        exits = listOf(
            ExitInfo("A1 Exit", "A1出口", "Wako Department Store and Seiko Clock Tower", listOf("Wako Clock Tower", "Mitsuokoshi")),
            ExitInfo("A3 Exit", "A3出口", "Direct underground passage to GINZA SIX luxury complex", listOf("GINZA SIX", "Chuo-dori Boulevard")),
            ExitInfo("B4 Exit", "B4出口", "Sukiyabashi Crossing, Sony Park, and Yurakucho connection", listOf("Ginza Sony Park", "Tokyu Plaza Ginza"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("G", "Ginza Line", "Shibuya Terminal", "渋谷行", 2, "Plat 1", "On Time"),
            DepartureInfo("M", "Marunouchi", "Tokyo / Ikebukuro", "東京・池袋行", 3, "Plat 3", "On Time"),
            DepartureInfo("H", "Hibiya Line", "Roppongi / Naka-Meguro", "六本木・中目黒行", 5, "Plat 5", "On Time")
        ),
        addressJa = "東京都中央区銀座四丁目1-2",
        isMajorHub = true
    )

    val stationRoppongi = Station(
        id = "sta_roppongi",
        nameEn = "Roppongi",
        nameJa = "六本木",
        kana = "ろっぽんぎ",
        latitude = 35.6628,
        longitude = 139.7314,
        lines = listOf(lineHibiya, lineOedo),
        operators = setOf(RailOperator.TOKYO_METRO, RailOperator.TOEI_SUBWAY),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Lockers at 1C concourse (Tokyo Midtown) & Exit 3 (Roppongi Hills)", "六本木ヒルズ連絡通路・東京ミッドタウン改札にロッカー", true),
            StationAmenity(AmenityType.ELEVATOR, "Express elevators serving deep Oedo Line platform (42m underground)", "地下42m大江戸線ホーム直通高速エレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "Toei Subway Free Wi-Fi & Tokyo Metro Free Wi-Fi", "都営・メトロ共通無料Wi-Fi", true),
            StationAmenity(AmenityType.RESTROOM, "Barrier-free accessible restrooms on B1 and B2 levels", "地下1階・2階多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Subway concourse Lawson & kiosks", "改札前ローソン・売店", true)
        ),
        platforms = listOf(
            PlatformInfo("1", "H", "Hibiya Line", "Ebisu, Naka-Meguro", "恵比寿・中目黒方面"),
            PlatformInfo("2", "H", "Hibiya Line", "Ginza, Ueno, Kitasenju", "銀座・上野・北千住方面"),
            PlatformInfo("1", "E", "Toei Oedo Line", "Daimon (Tokyo Tower), Ryogoku", "大門（東京タワー）・両国方面"),
            PlatformInfo("2", "E", "Toei Oedo Line", "Shinjuku, Tochomae, Hikarigaoka", "新宿・都庁前・光が丘方面")
        ),
        exits = listOf(
            ExitInfo("1C Exit", "1C出口", "Direct indoor concourse to Roppongi Hills & Mori Art Museum", listOf("Roppongi Hills", "Mori Art Museum", "TOHO Cinemas")),
            ExitInfo("8 Exit", "8出口", "Direct underground pedestrian walkway to Tokyo Midtown & 21_21", listOf("Tokyo Midtown", "Suntory Museum", "The Ritz-Carlton")),
            ExitInfo("3 Exit", "3出口", "Roppongi Crossing, nightlife, and Don Quijote Roppongi", listOf("Roppongi Crossing", "Don Quijote", "Nightlife"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("E", "Toei Oedo", "Shinjuku / Tochomae", "新宿・都庁前行", 2, "Plat E2", "On Time"),
            DepartureInfo("H", "Hibiya Line", "Ginza / Ueno", "銀座・上野行", 4, "Plat H2", "On Time"),
            DepartureInfo("E", "Toei Oedo", "Daimon / Tokyo Tower", "大門（東京タワー）行", 6, "Plat E1", "On Time")
        ),
        addressJa = "東京都港区六本木六丁目4-1",
        isMajorHub = false
    )

    val stationHarajuku = Station(
        id = "sta_harajuku",
        nameEn = "Harajuku / Meiji-Jingumae",
        nameJa = "原宿 / 明治神宮前",
        kana = "はらじゅく",
        latitude = 35.6702,
        longitude = 139.7027,
        lines = listOf(lineYamanote, lineChiyoda, lineFukutoshin),
        operators = setOf(RailOperator.JR_EAST, RailOperator.TOKYO_METRO),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "East Exit concourse lockers and Jingubashi lockers", "東口改札横・神宮橋ロッカー", true),
            StationAmenity(AmenityType.ELEVATOR, "Barrier-free path directly into Meiji Jingu Shrine forest grounds", "明治神宮境内・表参道方面エレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "JR-EAST Free Wi-Fi & Shibuya Wi-Fi", "JR東日本・渋谷フリーWi-Fi", true),
            StationAmenity(AmenityType.RESTROOM, "Modern spacious restrooms at New Station Building", "新駅舎内バリアフリートイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Sarutahiko Coffee & NewDays at Harajuku 2F terrace", "猿田彦珈琲・NewDays原宿新駅舎店", true)
        ),
        platforms = listOf(
            PlatformInfo("1", "JY", "JR Yamanote (Clockwise)", "Shibuya, Shinagawa", "渋谷・品川方面（内回り）"),
            PlatformInfo("2", "JY", "JR Yamanote (Counter-clockwise)", "Shinjuku, Ikebukuro", "新宿・池袋方面（外回り）"),
            PlatformInfo("C1-2", "C", "Tokyo Metro Chiyoda", "Omotesando, Otemachi", "表参道・大手町方面"),
            PlatformInfo("F1-2", "F", "Tokyo Metro Fukutoshin", "Shinjuku-sanchome, Ikebukuro", "新宿三丁目・池袋方面")
        ),
        exits = listOf(
            ExitInfo("Takeshita Exit", "竹下口", "Direct entry to world-famous Takeshita Street (crepes, fashion)", listOf("Takeshita Street", "Cat Street")),
            ExitInfo("East Exit", "東口", "Modern glass station entrance facing Omotesando shopping avenue", listOf("WITH HARAJUKU", "Tokyu Plaza Omotesando")),
            ExitInfo("West Exit", "西口", "Direct pedestrian bridge into sacred Meiji Jingu Shrine forest", listOf("Meiji Jingu Shrine", "Yoyogi Park"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("JY", "Yamanote Inner", "Shibuya / Shinagawa", "渋谷・品川方面", 2, "Plat 1", "On Time"),
            DepartureInfo("JY", "Yamanote Outer", "Shinjuku / Ikebukuro", "新宿・池袋方面", 3, "Plat 2", "On Time"),
            DepartureInfo("C", "Chiyoda Line", "Omotesando / Otemachi", "表参道・大手町行", 4, "Plat C1", "On Time")
        ),
        addressJa = "東京都渋谷区神宮前一丁目18-20",
        isMajorHub = false
    )

    val stationAsakusa = Station(
        id = "sta_asakusa",
        nameEn = "Asakusa",
        nameJa = "浅草",
        kana = "あさくさ",
        latitude = 35.7118,
        longitude = 139.7967,
        lines = listOf(lineGinza, lineToeiAsakusa),
        operators = setOf(RailOperator.TOKYO_METRO, RailOperator.TOEI_SUBWAY, RailOperator.TOBU, RailOperator.TSUKUBA),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Lockers at Kaminarimon Exit and Tobu Asakusa terminal concourse", "雷門前改札・東武浅草駅連絡口にロッカー", true),
            StationAmenity(AmenityType.ELEVATOR, "Elevators connecting Ginza line to Kaminarimon street level", "雷門通り地上行きエレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "Taito City Free Wi-Fi & Tokyo Metro Free Wi-Fi", "台東区フリーWi-Fi・メトロWi-Fi", true),
            StationAmenity(AmenityType.RESTROOM, "Clean multi-purpose restrooms with tourist baby amenities", "雷門改札内多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Ekimise Asakusa department store, traditional souvenir shops", "エキミセ浅草・伝統工芸土産店", true)
        ),
        platforms = listOf(
            PlatformInfo("1-2", "G", "Tokyo Metro Ginza", "Ueno, Ginza, Shibuya", "上野・銀座・渋谷方面（始発）"),
            PlatformInfo("A1", "A", "Toei Asakusa Line", "Oshiage (Skytree), Narita Airport", "押上（スカイツリー）・成田空港方面"),
            PlatformInfo("A2", "A", "Toei Asakusa Line", "Nihombashi, Shinagawa, Haneda Airport", "日本橋・品川・羽田空港方面")
        ),
        exits = listOf(
            ExitInfo("1 Exit (Kaminarimon)", "1番出口（雷門）", "Sensō-ji Temple, iconic giant red paper lantern, Nakamise-dori", listOf("Sensō-ji Temple", "Kaminarimon Gate", "Nakamise Street")),
            ExitInfo("4 Exit (Azumabashi)", "4番出口（吾妻橋）", "Sumida River boat cruise pier, Asahi Beer Flamme d'Or, Skytree view", listOf("Tokyo Water Bus", "Sumida Park", "Asahi Flame")),
            ExitInfo("7 Exit (Tobu Railway)", "7番出口（東武線）", "Transfer to Tobu Nikko Express Spacia X to Nikko & Kinugawa", listOf("Tobu Nikko Line", "Spacia X", "Ekimise"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("G", "Ginza Line", "Ueno / Ginza / Shibuya", "上野・銀座・渋谷行（当駅始発）", 1, "Plat G1", "Originating"),
            DepartureInfo("A", "Asakusa Line", "Haneda Airport Rapid", "羽田空港第1・第2行 エアポート快特", 4, "Plat A2", "Rapid", true),
            DepartureInfo("A", "Asakusa Line", "Narita Airport Access", "成田空港行 アクセス特急", 9, "Plat A1", "Express", true)
        ),
        addressJa = "東京都台東区浅草一丁目1-3",
        isMajorHub = true
    )

    val stationShinagawa = Station(
        id = "sta_shinagawa",
        nameEn = "Shinagawa",
        nameJa = "品川",
        kana = "しながわ",
        latitude = 35.6284,
        longitude = 139.7387,
        lines = listOf(lineYamanote, lineKeihinTohoku),
        operators = setOf(RailOperator.JR_EAST, RailOperator.SHINKANSEN),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Central passage lockers with real-time vacancy displays", "中央改札内空き状況モニター付き大型ロッカー", true),
            StationAmenity(AmenityType.ELEVATOR, "Full barrier-free access to all 15 platforms & Shinkansen", "全15ホーム・新幹線乗換口にエレベーター完備", true),
            StationAmenity(AmenityType.FREE_WIFI, "JR-EAST Free Wi-Fi throughout concourse", "JR東日本フリーWi-Fi", true),
            StationAmenity(AmenityType.TICKET_OFFICE, "JR East Travel Center & JR Central Shinkansen Ticket Office", "JR東日本訪日旅行センター・新幹線きっぷうりば", true),
            StationAmenity(AmenityType.RESTROOM, "Spacious universal restrooms with ostomate facilities", "エキュート品川内・改札内多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Ecute Shinagawa gourmet market, Ekiben, and cafes", "エキュート品川・有名駅弁・カフェ多数", true)
        ),
        platforms = listOf(
            PlatformInfo("1", "JY", "JR Yamanote (Clockwise)", "Tokyo, Ueno", "東京・上野方面（内回り）"),
            PlatformInfo("2", "JY", "JR Yamanote (Counter-clockwise)", "Shibuya, Shinjuku", "渋谷・新宿方面（外回り）"),
            PlatformInfo("3", "JK", "JR Keihin-Tohoku", "Tokyo, Omiya", "東京・大宮方面"),
            PlatformInfo("4", "JK", "JR Keihin-Tohoku", "Kawasaki, Yokohama", "川崎・横浜方面"),
            PlatformInfo("Keikyu", "KK", "Keikyu Main Line", "Haneda Airport Terminal 1·2·3 (11 min)", "羽田空港第1・2・3ターミナル方面")
        ),
        exits = listOf(
            ExitInfo("Takanawa Exit", "高輪口", "Hotel district (Grand Prince, Shinagawa Prince, Aqua Park aquarium)", listOf("Shinagawa Prince Hotel", "Maxell Aqua Park", "Wing Takanawa")),
            ExitInfo("Konan Exit", "港南口", "Modern corporate towers, Sony headquarters, and direct Haneda shuttle", listOf("Sony Building", "Shinagawa Intercity", "Bus Terminal"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("Keikyu", "Airport Rapid", "Haneda Airport (11m)", "羽田空港行 快特", 2, "Plat KK1", "Rapid", true),
            DepartureInfo("JY", "Yamanote Outer", "Shibuya / Shinjuku", "渋谷・新宿方面", 3, "Plat 2", "On Time"),
            DepartureInfo("JY", "Yamanote Inner", "Tokyo / Ueno", "東京・上野方面", 4, "Plat 1", "On Time")
        ),
        addressJa = "東京都港区高輪三丁目26-27",
        isMajorHub = true
    )

    val stationIkebukuro = Station(
        id = "sta_ikebukuro",
        nameEn = "Ikebukuro",
        nameJa = "池袋",
        kana = "いけぶくろ",
        latitude = 35.7295,
        longitude = 139.7109,
        lines = listOf(lineYamanote, lineMarunouchi, lineFukutoshin),
        operators = setOf(RailOperator.JR_EAST, RailOperator.TOKYO_METRO, RailOperator.SEIBU, RailOperator.TOBU),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Over 1,000 lockers across North, South, Central & Tobu/Seibu concourses", "北口・南口・中央口に1000台以上", true),
            StationAmenity(AmenityType.ELEVATOR, "Elevators connecting JR, Metro Marunouchi, Tobu & Seibu lines", "JR・丸ノ内線・東武・西武連絡エレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "JR-EAST Free Wi-Fi & Toshima City Free Wi-Fi", "JR公式・豊島区フリーWi-Fi完備", true),
            StationAmenity(AmenityType.TICKET_OFFICE, "Midori no Madoguchi at North & Central Ticket Gates", "みどりの窓口（中央改札・北改札）", true),
            StationAmenity(AmenityType.RESTROOM, "Barrier-free multi-purpose accessible restrooms at all gates", "全改札内外に多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Seibu Dept Store, Tobu Dept Store, Bic Camera, Animate flagship", "西武百貨店・東武百貨店・アニメイト本店直通", true)
        ),
        platforms = listOf(
            PlatformInfo("5-6", "JY", "JR Yamanote (Clockwise)", "Shinjuku, Shibuya", "新宿・渋谷方面（内回り）"),
            PlatformInfo("7-8", "JY", "JR Yamanote (Counter-clockwise)", "Tabata, Ueno", "田端・上野方面（外回り）"),
            PlatformInfo("M1-2", "M", "Tokyo Metro Marunouchi", "Ochanomizu, Tokyo, Ginza", "御茶ノ水・東京・銀座方面（始発）"),
            PlatformInfo("F1-2", "F", "Tokyo Metro Fukutoshin", "Shinjuku-sanchome, Shibuya, Yokohama", "新宿三丁目・渋谷・横浜方面")
        ),
        exits = listOf(
            ExitInfo("East Exit", "東口", "Sunshine City 60, Animate Flagship Store, Otome Road, Seibu", listOf("Sunshine City", "Animate Main Store", "Seibu Ikebukuro")),
            ExitInfo("West Exit", "西口", "Tokyo Metropolitan Theatre, Tobu Dept Store, Rikkyo University", listOf("Tokyo Metropolitan Theatre", "Tobu Department Store", "West Gate Park"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("M", "Marunouchi", "Tokyo / Ginza (Originating)", "東京・銀座行（当駅始発）", 1, "Plat M1", "Originating"),
            DepartureInfo("JY", "Yamanote Inner", "Shinjuku / Shibuya", "新宿・渋谷方面", 2, "Plat 5", "On Time"),
            DepartureInfo("JY", "Yamanote Outer", "Ueno / Tokyo", "上野・東京方面", 4, "Plat 7", "On Time")
        ),
        addressJa = "東京都豊島区南池袋一丁目28-2",
        isMajorHub = true
    )

    val stationShimbashi = Station(
        id = "sta_shimbashi",
        nameEn = "Shimbashi",
        nameJa = "新橋",
        kana = "しんばし",
        latitude = 35.6664,
        longitude = 139.7583,
        lines = listOf(lineYamanote, lineKeihinTohoku, lineGinza, lineToeiAsakusa),
        operators = setOf(RailOperator.JR_EAST, RailOperator.TOKYO_METRO, RailOperator.TOEI_SUBWAY, RailOperator.YURIKAMOME, RailOperator.MONORAIL),
        amenities = listOf(
            StationAmenity(AmenityType.COIN_LOCKERS, "Lockers at Karasumori Gate and SL Square underground passage", "烏森口・SL広場地下通路にロッカー多数", true),
            StationAmenity(AmenityType.ELEVATOR, "Elevators to Ginza Line & Yurikamome monorail to Odaiba", "銀座線・ゆりかもめお台場方面エレベーター", true),
            StationAmenity(AmenityType.FREE_WIFI, "JR-EAST Free Wi-Fi throughout", "JR公式フリーWi-Fi", true),
            StationAmenity(AmenityType.TICKET_OFFICE, "Midori no Madoguchi at North Gate", "北改札みどりの窓口", true),
            StationAmenity(AmenityType.RESTROOM, "Multi-purpose accessible restrooms inside Karasumori Gate", "烏森改札内多機能トイレ", true),
            StationAmenity(AmenityType.CONVENIENCE, "Wing Shimbashi shopping mall and NewDays", "ウィング新橋・売店多数", true)
        ),
        platforms = listOf(
            PlatformInfo("4", "JY", "JR Yamanote (Clockwise)", "Shinagawa, Shibuya", "品川・渋谷方面（内回り）"),
            PlatformInfo("5", "JY", "JR Yamanote (Counter-clockwise)", "Tokyo, Ueno", "東京・上野方面（外回り）"),
            PlatformInfo("G1-2", "G", "Tokyo Metro Ginza", "Ginza, Asakusa / Shibuya", "銀座・浅草／渋谷方面"),
            PlatformInfo("A1-2", "A", "Toei Asakusa Line", "Haneda Airport / Narita Airport", "羽田空港／成田空港方面")
        ),
        exits = listOf(
            ExitInfo("Hibiya Exit (SL Square)", "日比谷口（SL広場）", "Famous steam locomotive monument, TV news interview spot", listOf("SL Steam Locomotive Plaza", "Ginza West")),
            ExitInfo("Karasumori Exit", "烏森口", "Vibrant izakaya bar alleys and lively corporate dining", listOf("Karasumori Shrine", "Dining Alleys")),
            ExitInfo("Shiodome Exit", "汐留口", "Direct pedestrian skybridge to Yurikamome monorail and Shiodome", listOf("Yurikamome to Odaiba", "Shiodome Sio-Site", "NTV Tower"))
        ),
        baseIcFareYen = 178,
        baseTicketFareYen = 180,
        upcomingDepartures = listOf(
            DepartureInfo("JY", "Yamanote Inner", "Shinagawa / Shibuya", "品川・渋谷方面", 2, "Plat 4", "On Time"),
            DepartureInfo("G", "Ginza Line", "Ginza / Asakusa", "銀座・浅草行", 3, "Plat G2", "On Time"),
            DepartureInfo("A", "Asakusa Line", "Haneda Airport Rapid", "羽田空港行 快特", 5, "Plat A1", "Rapid", true)
        ),
        addressJa = "東京都港区新橋二丁目17",
        isMajorHub = true
    )

    val allStations = listOf(
        stationShinjuku,
        stationShibuya,
        stationTokyo,
        stationAkihabara,
        stationUeno,
        stationGinza,
        stationRoppongi,
        stationHarajuku,
        stationAsakusa,
        stationShinagawa,
        stationIkebukuro,
        stationShimbashi
    )

    // Yamanote line sequence for realistic route mapping
    val yamanoteStationOrder = listOf(
        stationTokyo, stationShimbashi, stationShinagawa,
        stationShibuya, stationHarajuku, stationShinjuku,
        stationIkebukuro, stationUeno, stationAkihabara,
        stationTokyo
    )

    // Coordinates for drawing the iconic Yamanote Green circle track
    val yamanoteTrackPolyline = listOf(
        35.6812 to 139.7671, // Tokyo
        35.6750 to 139.7630, // Yurakucho
        35.6664 to 139.7583, // Shimbashi
        35.6550 to 139.7560, // Hamamatsucho
        35.6457 to 139.7476, // Tamachi
        35.6284 to 139.7387, // Shinagawa
        35.6197 to 139.7285, // Osaki
        35.6264 to 139.7234, // Gotanda
        35.6339 to 139.7158, // Meguro
        35.6467 to 139.7101, // Ebisu
        35.6580 to 139.7016, // Shibuya
        35.6702 to 139.7027, // Harajuku
        35.6820 to 139.7000, // Yoyogi
        35.6896 to 139.7006, // Shinjuku
        35.7013 to 139.7002, // Shin-Okubo
        35.7126 to 139.7038, // Takadanobaba
        35.7212 to 139.7066, // Mejiro
        35.7295 to 139.7109, // Ikebukuro
        35.7318 to 139.7281, // Otsuka
        35.7335 to 139.7396, // Sugamo
        35.7365 to 139.7470, // Komagome
        35.7381 to 139.7608, // Tabata
        35.7321 to 139.7668, // Nishi-Nippori
        35.7278 to 139.7710, // Nippori
        35.7205 to 139.7788, // Uguisudani
        35.7141 to 139.7774, // Ueno
        35.7075 to 139.7745, // Okachimachi
        35.6983 to 139.7731, // Akihabara
        35.6917 to 139.7709, // Kanda
        35.6812 to 139.7671  // Tokyo loop back
    )

    // Chuo line cutting east-west
    val chuoTrackPolyline = listOf(
        35.6896 to 139.7006, // Shinjuku
        35.6860 to 139.7306, // Yotsuya
        35.6997 to 139.7650, // Ochanomizu
        35.6983 to 139.7731, // Akihabara
        35.6917 to 139.7709, // Kanda
        35.6812 to 139.7671  // Tokyo
    )

    // Metro Ginza line
    val ginzaTrackPolyline = listOf(
        35.6580 to 139.7016, // Shibuya
        35.6653 to 139.7123, // Omotesando
        35.6698 to 139.7180, // Aoyama-itchome
        35.6766 to 139.7368, // Akasaka-mitsuke
        35.6705 to 139.7445, // Toranomon
        35.6664 to 139.7583, // Shimbashi
        35.6719 to 139.7640, // Ginza
        35.6814 to 139.7744, // Nihombashi
        35.6917 to 139.7720, // Kanda
        35.7141 to 139.7774, // Ueno
        35.7118 to 139.7967  // Asakusa
    )

    // Metro Marunouchi line
    val marunouchiTrackPolyline = listOf(
        35.7000 to 139.6500, // Ogikubo
        35.6896 to 139.7006, // Shinjuku
        35.6908 to 139.7088, // Shinjuku-sanchome
        35.6860 to 139.7306, // Yotsuya
        35.6766 to 139.7368, // Akasaka-mitsuke
        35.6740 to 139.7500, // Kasumigaseki
        35.6719 to 139.7640, // Ginza
        35.6812 to 139.7671, // Tokyo
        35.6865 to 139.7640, // Otemachi
        35.7000 to 139.7600, // Ochanomizu
        35.7080 to 139.7520, // Korakuen
        35.7295 to 139.7109  // Ikebukuro
    )
}
