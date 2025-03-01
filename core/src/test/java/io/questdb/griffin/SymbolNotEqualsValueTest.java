/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2023 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.griffin;

import org.junit.Test;

public class SymbolNotEqualsValueTest extends AbstractGriffinTest {

    @Test
    public void testNotEquals1SymbolsNonExistent() throws Exception {
        final String expected = "k\tprice\tts\n" +
                "ABB\t0.8043224099968393\t1970-01-03T00:00:00.000000Z\n" +
                "DXR\t0.08486964232560668\t1970-01-03T00:06:00.000000Z\n" +
                "DXR\t0.0843832076262595\t1970-01-03T00:12:00.000000Z\n" +
                "HBC\t0.6508594025855301\t1970-01-03T00:18:00.000000Z\n" +
                "HBC\t0.7905675319675964\t1970-01-03T00:24:00.000000Z\n" +
                "ABB\t0.22452340856088226\t1970-01-03T00:30:00.000000Z\n" +
                "ABB\t0.3491070363730514\t1970-01-03T00:36:00.000000Z\n" +
                "ABB\t0.7611029514995744\t1970-01-03T00:42:00.000000Z\n" +
                "ABB\t0.4217768841969397\t1970-01-03T00:48:00.000000Z\n" +
                "HBC\t0.0367581207471136\t1970-01-03T00:54:00.000000Z\n";

        assertQuery(
                "k\tprice\tts\n",
                "select sym k, price, ts from x where sym != 'AAA'",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                "ts",
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "    from long_sequence(10)) timestamp (ts)",
                expected,
                true
        );
    }

    @Test
    public void testNotEquals1SymbolsWithConstantFilter() throws Exception {
        final String expected = "k\tj\tprice\tts\n";

        assertQuery(
                "k\tj\tprice\tts\n",
                "select sym k, sym2 j, price, ts from x where sym != 'ABB' and 2 = 1",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    sym2 symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                "ts",
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_symbol('D', 'E', 'F') sym2, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "        from long_sequence(10)) timestamp (ts)",
                expected,
                false,
                true,
                true
        );
    }

    @Test
    public void testNotEquals1SymbolsWithEqualsAnother() throws Exception {
        final String expected = "k\tprice\tts\tj\n" +
                "DXR\t0.6778564558839208\t1970-01-03T00:48:00.000000Z\tF\n" +
                "DXR\t0.299199045961845\t1970-01-03T00:06:00.000000Z\tF\n";

        assertQuery(
                "k\tprice\tts\tj\n",
                "select sym k, price, ts, sym2 j from x where sym2 = 'F' AND sym != 'ABB' order by k, j desc",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    sym2 symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                null,
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_symbol('D', 'E', 'F') sym2, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "        from long_sequence(10)) timestamp (ts)",
                expected,
                true
        );
    }

    @Test
    public void testNotEquals1SymbolsWithFilter() throws Exception {
        final String expected = "k\tj\tprice\tts\n" +
                "HBC\tE\t0.9856290845874263\t1970-01-03T00:18:00.000000Z\n" +
                "HBC\tD\t0.7611029514995744\t1970-01-03T00:30:00.000000Z\n" +
                "DXR\tF\t0.6778564558839208\t1970-01-03T00:48:00.000000Z\n";

        assertQuery(
                "k\tj\tprice\tts\n",
                "select sym k, sym2 j, price, ts from x where sym != 'ABB' and price > 0.5",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    sym2 symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                "ts",
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_symbol('D', 'E', 'F') sym2, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "        from long_sequence(10)) timestamp (ts)",
                expected,
                true
        );
    }

    @Test
    public void testNotEquals1SymbolsWithInvariantOrderBy() throws Exception {
        final String expected = "k\tprice\tts\n" +
                "DXR\t0.0843832076262595\t1970-01-03T00:12:00.000000Z\n" +
                "DXR\t0.08486964232560668\t1970-01-03T00:06:00.000000Z\n" +
                "HBC\t0.0367581207471136\t1970-01-03T00:54:00.000000Z\n" +
                "HBC\t0.7905675319675964\t1970-01-03T00:24:00.000000Z\n" +
                "HBC\t0.6508594025855301\t1970-01-03T00:18:00.000000Z\n";

        assertQuery(
                "k\tprice\tts\n",
                "select sym k, price, ts from x where sym != 'ABB' order by k",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                null,
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "    from long_sequence(10)) timestamp (ts)",
                expected,
                true
        );
    }

    @Test
    public void testNotEquals1SymbolsWithInvariantOrderBy2SymsDesc() throws Exception {
        final String expected = "k\tj\tprice\tts\n" +
                "DXR\tF\t0.6778564558839208\t1970-01-03T00:48:00.000000Z\n" +
                "DXR\tF\t0.299199045961845\t1970-01-03T00:06:00.000000Z\n" +
                "DXR\tD\t0.38539947865244994\t1970-01-03T00:54:00.000000Z\n" +
                "HBC\tE\t0.9856290845874263\t1970-01-03T00:18:00.000000Z\n" +
                "HBC\tD\t0.2390529010846525\t1970-01-03T00:42:00.000000Z\n" +
                "HBC\tD\t0.7611029514995744\t1970-01-03T00:30:00.000000Z\n";

        assertQuery(
                "k\tj\tprice\tts\n",
                "select sym k, sym2 j, price, ts from x where sym != 'ABB' order by k, j desc",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    sym2 symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                null,
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_symbol('D', 'E', 'F') sym2, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "        from long_sequence(10)) timestamp (ts)",
                expected,
                true
        );
    }

    @Test
    public void testNotEquals2Symbols() throws Exception {
        final String expected = "k\tprice\tts\n" +
                "ABB\t0.8043224099968393\t1970-01-03T00:00:00.000000Z\n" +
                "DXR\t0.08486964232560668\t1970-01-03T00:06:00.000000Z\n" +
                "DXR\t0.0843832076262595\t1970-01-03T00:12:00.000000Z\n" +
                "ABB\t0.22452340856088226\t1970-01-03T00:30:00.000000Z\n" +
                "ABB\t0.3491070363730514\t1970-01-03T00:36:00.000000Z\n" +
                "ABB\t0.7611029514995744\t1970-01-03T00:42:00.000000Z\n" +
                "ABB\t0.4217768841969397\t1970-01-03T00:48:00.000000Z\n";
        assertQuery(
                "k\tprice\tts\n",
                "select sym k, price, ts from x where sym != 'HBC' and sym != 'AAA'",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                "ts",
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "    from long_sequence(10)) timestamp (ts)",
                expected
        );
        // insert query values:
        //
        // sym	price	ts
        // ABB	0.8043224099968393	1970-01-03T00:00:00.000000Z (selected)
        // DXR	0.08486964232560668	1970-01-03T00:06:00.000000Z (selected)
        // DXR	0.0843832076262595	1970-01-03T00:12:00.000000Z (selected)
        // HBC	0.6508594025855301	1970-01-03T00:18:00.000000Z
        // HBC	0.7905675319675964	1970-01-03T00:24:00.000000Z
        // ABB	0.22452340856088226	1970-01-03T00:30:00.000000Z (selected)
        // ABB	0.3491070363730514	1970-01-03T00:36:00.000000Z (selected)
        // ABB	0.7611029514995744	1970-01-03T00:42:00.000000Z (selected)
        // ABB	0.4217768841969397	1970-01-03T00:48:00.000000Z (selected)
        // HBC	0.0367581207471136	1970-01-03T00:54:00.000000Z
        //
        // sym can only take values from 'ABB', 'HBC', 'DXR'
        //
        // condition: where sym != 'HBC' and sym != 'AAA'
        // equivalent: sym = 'ABB' or sym = ''DXR'
        // equivalent: sym != 'HBC' (optimisation)
    }

    @Test
    public void testNotEqualsSingleSymbol() throws Exception {
        final String expected = "k\tprice\tts\n" +
                "ABB\t0.8043224099968393\t1970-01-03T00:00:00.000000Z\n" +
                "DXR\t0.08486964232560668\t1970-01-03T00:06:00.000000Z\n" +
                "DXR\t0.0843832076262595\t1970-01-03T00:12:00.000000Z\n" +
                "ABB\t0.22452340856088226\t1970-01-03T00:30:00.000000Z\n" +
                "ABB\t0.3491070363730514\t1970-01-03T00:36:00.000000Z\n" +
                "ABB\t0.7611029514995744\t1970-01-03T00:42:00.000000Z\n" +
                "ABB\t0.4217768841969397\t1970-01-03T00:48:00.000000Z\n";

        assertQuery(
                "k\tprice\tts\n",
                "select sym k, price, ts from x where sym != 'HBC'",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                "ts",
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "    from long_sequence(10)) timestamp (ts)",
                expected,
                true
        );
    }

    @Test
    public void testNotEqualsSingleSymbolNoCache() throws Exception {
        final String expected = "k\tprice\tts\n" +
                "ABB\t0.8043224099968393\t1970-01-03T00:00:00.000000Z\n" +
                "DXR\t0.08486964232560668\t1970-01-03T00:06:00.000000Z\n" +
                "DXR\t0.0843832076262595\t1970-01-03T00:12:00.000000Z\n" +
                "ABB\t0.22452340856088226\t1970-01-03T00:30:00.000000Z\n" +
                "ABB\t0.3491070363730514\t1970-01-03T00:36:00.000000Z\n" +
                "ABB\t0.7611029514995744\t1970-01-03T00:42:00.000000Z\n" +
                "ABB\t0.4217768841969397\t1970-01-03T00:48:00.000000Z\n";

        assertQuery(
                "k\tprice\tts\n",
                "select sym k, price, ts from x where sym != 'HBC'",
                "create table x (\n" +
                        "    sym symbol nocache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                "ts",
                "insert into x select * from (select rnd_symbol('ABB', 'HBC', 'DXR') sym, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "    from long_sequence(10)) timestamp (ts)",
                expected,
                true
        );
    }

    @Test
    public void testSymbolNotEqualsWhenNumberOfSymbolsExceedTheConfigMax() throws Exception {
        final String expected = "k\tprice\tts\n" +
                "UGS\t0.2093569947644236\t1970-01-03T00:00:00.000000Z\n" +
                "OUS\t0.8439276969435359\t1970-01-03T00:06:00.000000Z\n" +
                "ZIM\t0.03973283003449557\t1970-01-03T00:12:00.000000Z\n" +
                "XZO\t0.8551850405049611\t1970-01-03T00:18:00.000000Z\n" +
                "OVL\t0.7769285766561033\t1970-01-03T00:24:00.000000Z\n" +
                "EHN\t0.6226001464598434\t1970-01-03T00:30:00.000000Z\n" +
                "WVD\t0.7195457109208119\t1970-01-03T00:36:00.000000Z\n" +
                "UOJ\t0.23493793601747937\t1970-01-03T00:42:00.000000Z\n" +
                "OON\t0.6334964081687151\t1970-01-03T00:48:00.000000Z\n" +
                "OVL\t0.95820305972778\t1970-01-03T00:54:00.000000Z\n" +
                "KYL\t0.9130151105125102\t1970-01-03T01:00:00.000000Z\n" +
                "KKH\t0.17405556853190263\t1970-01-03T01:06:00.000000Z\n" +
                "ETJ\t0.6887925530449002\t1970-01-03T01:12:00.000000Z\n" +
                "BEO\t0.5796722100538578\t1970-01-03T01:18:00.000000Z\n" +
                "SED\t0.7530494527849502\t1970-01-03T01:24:00.000000Z\n" +
                "OXP\t0.8775452659546193\t1970-01-03T01:30:00.000000Z\n" +
                "WIF\t0.19736767249829557\t1970-01-03T01:36:00.000000Z\n" +
                "RIP\t0.3121271759430503\t1970-01-03T01:42:00.000000Z\n" +
                "SWH\t0.5626370294064983\t1970-01-03T01:48:00.000000Z\n" +
                "VTJ\t0.49199001716312474\t1970-01-03T01:54:00.000000Z\n" +
                "YPH\t0.18746631995449403\t1970-01-03T02:00:00.000000Z\n" +
                "SHR\t0.5779007672652298\t1970-01-03T02:06:00.000000Z\n" +
                "GWF\t0.33976095270593043\t1970-01-03T02:12:00.000000Z\n" +
                "XWC\t0.8402964708129546\t1970-01-03T02:18:00.000000Z\n" +
                "MPG\t0.13210005359166366\t1970-01-03T02:24:00.000000Z\n" +
                "MBE\t0.03192108074989719\t1970-01-03T02:30:00.000000Z\n" +
                "VTM\t0.17498425722537903\t1970-01-03T02:36:00.000000Z\n" +
                "ZXI\t0.34257201464152764\t1970-01-03T02:42:00.000000Z\n" +
                "BND\t0.9790787740413469\t1970-01-03T02:48:00.000000Z\n" +
                "PEH\t0.7527907209539796\t1970-01-03T02:54:00.000000Z\n" +
                "RSZ\t0.9546417330809595\t1970-01-03T03:00:00.000000Z\n" +
                "OON\t0.1479745625593103\t1970-01-03T03:06:00.000000Z\n" +
                "KRG\t0.8115426881784433\t1970-01-03T03:12:00.000000Z\n" +
                "LUO\t0.32093405888189597\t1970-01-03T03:18:00.000000Z\n" +
                "OVL\t0.04321289940104611\t1970-01-03T03:24:00.000000Z\n" +
                "XZO\t0.6612090806765161\t1970-01-03T03:30:00.000000Z\n" +
                "XWC\t0.0031075670450616544\t1970-01-03T03:36:00.000000Z\n" +
                "UVS\t0.865629565918467\t1970-01-03T03:42:00.000000Z\n" +
                "RMF\t0.970570224065161\t1970-01-03T03:48:00.000000Z\n" +
                "OUS\t0.7198854503668188\t1970-01-03T03:54:00.000000Z\n" +
                "HZS\t0.31617860377666984\t1970-01-03T04:00:00.000000Z\n" +
                "RFB\t0.21498295033639603\t1970-01-03T04:06:00.000000Z\n" +
                "YHB\t0.5475429391562822\t1970-01-03T04:12:00.000000Z\n" +
                "HML\t0.8514849800664227\t1970-01-03T04:18:00.000000Z\n" +
                "KVV\t0.4268921400209912\t1970-01-03T04:24:00.000000Z\n" +
                "KYL\t0.48782086416459025\t1970-01-03T04:30:00.000000Z\n" +
                "OLY\t0.13312214396754163\t1970-01-03T04:36:00.000000Z\n" +
                "OVI\t0.9482880758785679\t1970-01-03T04:42:00.000000Z\n" +
                "LUO\t0.7039785408034679\t1970-01-03T04:48:00.000000Z\n" +
                "RIP\t0.5083087912946505\t1970-01-03T04:54:00.000000Z\n" +
                "RIP\t0.8136066472617021\t1970-01-03T05:00:00.000000Z\n" +
                "BEO\t0.3058008320091107\t1970-01-03T05:06:00.000000Z\n" +
                "UOJ\t0.034652347087289925\t1970-01-03T05:12:00.000000Z\n" +
                "JUM\t0.5900836401674938\t1970-01-03T05:18:00.000000Z\n" +
                "FYU\t0.1460524999338917\t1970-01-03T05:24:00.000000Z\n" +
                "NVT\t0.04211401699125483\t1970-01-03T05:30:00.000000Z\n" +
                "HGO\t0.7620812803991436\t1970-01-03T05:36:00.000000Z\n" +
                "NZZ\t0.2703179181043681\t1970-01-03T05:42:00.000000Z\n" +
                "VQE\t0.2000682450929353\t1970-01-03T05:48:00.000000Z\n" +
                "FKW\t0.7020445151566204\t1970-01-03T05:54:00.000000Z\n" +
                "DOT\t0.1389067130304884\t1970-01-03T06:00:00.000000Z\n" +
                "KRG\t0.1599211504269954\t1970-01-03T06:06:00.000000Z\n" +
                "BRO\t0.33828954246335896\t1970-01-03T06:12:00.000000Z\n" +
                "XWC\t0.18967967822948184\t1970-01-03T06:18:00.000000Z\n" +
                "BRO\t0.6053450223895661\t1970-01-03T06:24:00.000000Z\n" +
                "IIH\t0.06027878760582406\t1970-01-03T06:30:00.000000Z\n" +
                "KUI\t0.5160053477987824\t1970-01-03T06:36:00.000000Z\n" +
                "KRG\t0.21047933106727745\t1970-01-03T06:42:00.000000Z\n" +
                "UIC\t0.04404000858917945\t1970-01-03T06:48:00.000000Z\n" +
                "KVV\t0.41496612044075665\t1970-01-03T06:54:00.000000Z\n" +
                "ZMZ\t0.36078878996232167\t1970-01-03T07:00:00.000000Z\n" +
                "LEO\t0.2325041018786207\t1970-01-03T07:06:00.000000Z\n" +
                "ELL\t0.10799057399629297\t1970-01-03T07:12:00.000000Z\n" +
                "SJO\t0.8353079103853974\t1970-01-03T07:18:00.000000Z\n" +
                "SJO\t0.11048000399634927\t1970-01-03T07:24:00.000000Z\n" +
                "ETJ\t0.6001215594928115\t1970-01-03T07:30:00.000000Z\n" +
                "YQE\t0.909668342880534\t1970-01-03T07:36:00.000000Z\n" +
                "ETJ\t0.6741248448728824\t1970-01-03T07:42:00.000000Z\n" +
                "XYS\t0.0652033813358841\t1970-01-03T07:48:00.000000Z\n" +
                "FYU\t0.8853675629694284\t1970-01-03T07:54:00.000000Z\n" +
                "PEH\t0.15274858078119136\t1970-01-03T08:00:00.000000Z\n" +
                "EHN\t0.7468602267994937\t1970-01-03T08:06:00.000000Z\n" +
                "DOT\t0.9976896430755934\t1970-01-03T08:12:00.000000Z\n" +
                "WEK\t0.7645280362997794\t1970-01-03T08:18:00.000000Z\n" +
                "YPH\t0.9316283568969537\t1970-01-03T08:24:00.000000Z\n" +
                "HFV\t0.33218666480522674\t1970-01-03T08:30:00.000000Z\n" +
                "BEO\t0.6940917925148332\t1970-01-03T08:36:00.000000Z\n" +
                "UED\t0.4595378556321077\t1970-01-03T08:42:00.000000Z\n" +
                "SED\t0.5380626833618448\t1970-01-03T08:48:00.000000Z\n" +
                "JMY\t0.7783351753890267\t1970-01-03T08:54:00.000000Z\n" +
                "KRG\t0.12454054765285283\t1970-01-03T09:00:00.000000Z\n" +
                "LPD\t0.2625424312419562\t1970-01-03T09:06:00.000000Z\n" +
                "VLT\t0.1350821238488883\t1970-01-03T09:12:00.000000Z\n" +
                "WVD\t0.4421551587238961\t1970-01-03T09:18:00.000000Z\n" +
                "TWN\t0.34491612561683394\t1970-01-03T09:24:00.000000Z\n" +
                "KUI\t0.5921457770297527\t1970-01-03T09:30:00.000000Z\n" +
                "TGQ\t0.8409080254825717\t1970-01-03T09:36:00.000000Z\n" +
                "KVV\t0.4031733414086601\t1970-01-03T09:42:00.000000Z\n" +
                "IBB\t0.39201296350741366\t1970-01-03T09:48:00.000000Z\n" +
                "UIC\t0.28813952005117305\t1970-01-03T09:54:00.000000Z\n" +
                "ZLU\t0.5064580751162086\t1970-01-03T10:00:00.000000Z\n" +
                "ZIM\t0.5765797240495835\t1970-01-03T10:06:00.000000Z\n" +
                "YSS\t0.7055404165623212\t1970-01-03T10:12:00.000000Z\n" +
                "WSW\t0.40410163160526613\t1970-01-03T10:18:00.000000Z\n" +
                "OUS\t0.8802810667279274\t1970-01-03T10:24:00.000000Z\n" +
                "HML\t0.4444125234732249\t1970-01-03T10:30:00.000000Z\n" +
                "ZXI\t0.9531459048178456\t1970-01-03T10:36:00.000000Z\n" +
                "QLD\t0.0567238328086237\t1970-01-03T10:42:00.000000Z\n" +
                "MBE\t0.22252546562577824\t1970-01-03T10:48:00.000000Z\n" +
                "MSS\t0.681606585145203\t1970-01-03T10:54:00.000000Z\n" +
                "ULO\t0.8869397617459538\t1970-01-03T11:00:00.000000Z\n" +
                "BEO\t0.5221781467839528\t1970-01-03T11:06:00.000000Z\n" +
                "UED\t0.2266157317795261\t1970-01-03T11:12:00.000000Z\n" +
                "GZS\t0.42558021324800144\t1970-01-03T11:18:00.000000Z\n" +
                "ZIM\t0.6068565916347403\t1970-01-03T11:24:00.000000Z\n" +
                "OLY\t0.004918542726028763\t1970-01-03T11:30:00.000000Z\n" +
                "HBH\t0.008134052047644613\t1970-01-03T11:36:00.000000Z\n" +
                "VLT\t0.6361737673041902\t1970-01-03T11:42:00.000000Z\n" +
                "ZIM\t0.28831783004973577\t1970-01-03T11:48:00.000000Z\n" +
                "KFL\t0.3595576962747611\t1970-01-03T11:54:00.000000Z\n" +
                "KJS\t0.7999403044078355\t1970-01-03T12:00:00.000000Z\n" +
                "LGL\t0.5823910118974169\t1970-01-03T12:06:00.000000Z\n" +
                "KYL\t0.9694731343686098\t1970-01-03T12:12:00.000000Z\n" +
                "FYU\t0.9887681426881507\t1970-01-03T12:18:00.000000Z\n" +
                "NZZ\t0.39211484750712344\t1970-01-03T12:24:00.000000Z\n" +
                "HZS\t0.29120049877582566\t1970-01-03T12:30:00.000000Z\n" +
                "HZS\t0.6591146619441391\t1970-01-03T12:36:00.000000Z\n" +
                "FOW\t0.3504695674352035\t1970-01-03T12:42:00.000000Z\n" +
                "OMV\t0.28019218825051395\t1970-01-03T12:48:00.000000Z\n" +
                "ZLU\t0.3228786903275197\t1970-01-03T12:54:00.000000Z\n" +
                "LEO\t0.06052105248562101\t1970-01-03T13:00:00.000000Z\n" +
                "EHN\t0.7600550885615773\t1970-01-03T13:06:00.000000Z\n" +
                "ICC\t0.45278823120909895\t1970-01-03T13:12:00.000000Z\n" +
                "XUX\t0.026319297183393875\t1970-01-03T13:18:00.000000Z\n" +
                "HGO\t0.10663485323987387\t1970-01-03T13:24:00.000000Z\n" +
                "ZUL\t0.48573429889865705\t1970-01-03T13:30:00.000000Z\n" +
                "NRX\t0.8973562700864572\t1970-01-03T13:36:00.000000Z\n" +
                "IBB\t0.06820168647245783\t1970-01-03T13:42:00.000000Z\n" +
                "UGS\t0.5862806534829702\t1970-01-03T13:48:00.000000Z\n" +
                "VDZ\t0.828928908465152\t1970-01-03T13:54:00.000000Z\n" +
                "OON\t0.647875746786617\t1970-01-03T14:00:00.000000Z\n" +
                "ZMZ\t0.5157225592346661\t1970-01-03T14:06:00.000000Z\n" +
                "DOT\t0.16320835762949149\t1970-01-03T14:12:00.000000Z\n" +
                "XWC\t0.8203418140538824\t1970-01-03T14:18:00.000000Z\n" +
                "XYS\t0.8335063783919325\t1970-01-03T14:24:00.000000Z\n" +
                "ZIM\t0.535993442770838\t1970-01-03T14:30:00.000000Z\n" +
                "GZS\t0.2544317267472076\t1970-01-03T14:36:00.000000Z\n" +
                "OVL\t0.25604136769205754\t1970-01-03T14:42:00.000000Z\n" +
                "DOT\t0.9224004398482664\t1970-01-03T14:48:00.000000Z\n" +
                "OGX\t0.5785645380474713\t1970-01-03T14:54:00.000000Z\n" +
                "ELL\t0.3503522147575858\t1970-01-03T15:00:00.000000Z\n" +
                "VQE\t0.3209515177515627\t1970-01-03T15:06:00.000000Z\n" +
                "YYC\t0.12715627282156716\t1970-01-03T15:12:00.000000Z\n" +
                "HZS\t0.5330584032999529\t1970-01-03T15:18:00.000000Z\n" +
                "NRX\t0.23846285137007717\t1970-01-03T15:24:00.000000Z\n" +
                "MNX\t0.6367746812001958\t1970-01-03T15:30:00.000000Z\n" +
                "UGS\t0.6936669914583254\t1970-01-03T15:36:00.000000Z\n" +
                "SHR\t0.9578716688144072\t1970-01-03T15:42:00.000000Z\n" +
                "OVL\t0.17914853671380093\t1970-01-03T15:48:00.000000Z\n" +
                "HFV\t0.1319044042993568\t1970-01-03T15:54:00.000000Z\n" +
                "GWF\t0.5079751443209725\t1970-01-03T16:00:00.000000Z\n" +
                "RMF\t0.2703044758382739\t1970-01-03T16:06:00.000000Z\n" +
                "TGP\t0.6376518594972684\t1970-01-03T16:12:00.000000Z\n" +
                "JMY\t0.011263511839942453\t1970-01-03T16:18:00.000000Z\n" +
                "HML\t0.9176263114713273\t1970-01-03T16:24:00.000000Z\n" +
                "ZIM\t0.6281252905002019\t1970-01-03T16:30:00.000000Z\n" +
                "TWN\t0.2824076895992761\t1970-01-03T16:36:00.000000Z\n" +
                "XZO\t0.27144997281940675\t1970-01-03T16:42:00.000000Z\n" +
                "ZGH\t0.6931441108030082\t1970-01-03T16:48:00.000000Z\n" +
                "YHB\t0.837738444021418\t1970-01-03T16:54:00.000000Z\n" +
                "HBH\t0.8658616916564643\t1970-01-03T17:00:00.000000Z\n" +
                "YYC\t0.36986619304630497\t1970-01-03T17:06:00.000000Z\n" +
                "ZGH\t0.10424082472921137\t1970-01-03T17:12:00.000000Z\n" +
                "DOT\t0.9266929571641075\t1970-01-03T17:18:00.000000Z\n" +
                "UIC\t0.7751886508004251\t1970-01-03T17:24:00.000000Z\n" +
                "OMV\t0.7417434132166958\t1970-01-03T17:30:00.000000Z\n" +
                "UOJ\t0.9379038084870472\t1970-01-03T17:36:00.000000Z\n" +
                "UGS\t0.5350165471764692\t1970-01-03T17:42:00.000000Z\n" +
                "VTM\t0.3257868894353412\t1970-01-03T17:48:00.000000Z\n" +
                "XWC\t0.6927480038605662\t1970-01-03T17:54:00.000000Z\n" +
                "ZIM\t0.9410396704938232\t1970-01-03T18:00:00.000000Z\n" +
                "WCP\t0.9610592594899304\t1970-01-03T18:06:00.000000Z\n" +
                "GLO\t0.236380596505666\t1970-01-03T18:12:00.000000Z\n" +
                "SWH\t0.08533575092925538\t1970-01-03T18:18:00.000000Z\n" +
                "ELL\t0.8718394349472115\t1970-01-03T18:24:00.000000Z\n" +
                "KFL\t0.45388767393986074\t1970-01-03T18:30:00.000000Z\n" +
                "RSZ\t0.8413721135371649\t1970-01-03T18:36:00.000000Z\n" +
                "MBE\t0.527776712010911\t1970-01-03T18:42:00.000000Z\n" +
                "XWC\t0.4701492486769596\t1970-01-03T18:48:00.000000Z\n" +
                "SRY\t0.8139041928326346\t1970-01-03T18:54:00.000000Z\n" +
                "HML\t0.761296424148768\t1970-01-03T19:00:00.000000Z\n" +
                "OVI\t0.43990342764801993\t1970-01-03T19:06:00.000000Z\n" +
                "LPD\t0.3434029925127561\t1970-01-03T19:12:00.000000Z\n" +
                "GLO\t0.2875739269292986\t1970-01-03T19:18:00.000000Z\n" +
                "DOT\t0.9385037871004874\t1970-01-03T19:24:00.000000Z\n" +
                "OUS\t0.991107083990332\t1970-01-03T19:30:00.000000Z\n" +
                "MSS\t0.9154548873622441\t1970-01-03T19:36:00.000000Z\n" +
                "GWF\t0.030030519162390967\t1970-01-03T19:42:00.000000Z\n" +
                "UED\t0.970664272223612\t1970-01-03T19:48:00.000000Z\n" +
                "XZO\t0.9380189854875546\t1970-01-03T19:54:00.000000Z\n" +
                "DOT\t0.11763567185322699\t1970-01-03T20:00:00.000000Z\n" +
                "VQE\t0.31168586687815647\t1970-01-03T20:06:00.000000Z\n" +
                "TGQ\t0.1779779439502811\t1970-01-03T20:12:00.000000Z\n" +
                "ZGH\t0.38106875419134767\t1970-01-03T20:18:00.000000Z\n" +
                "VTM\t0.6627303823338926\t1970-01-03T20:24:00.000000Z\n" +
                "DEY\t0.5956163532141281\t1970-01-03T20:30:00.000000Z\n" +
                "ETJ\t0.4374309393168063\t1970-01-03T20:36:00.000000Z\n" +
                "FLR\t0.5505134691493859\t1970-01-03T20:42:00.000000Z\n" +
                "XWC\t0.3436802159856278\t1970-01-03T20:48:00.000000Z\n" +
                "RFB\t0.4176571781712538\t1970-01-03T20:54:00.000000Z\n" +
                "ZXI\t0.9605775051510881\t1970-01-03T21:00:00.000000Z\n" +
                "HFV\t0.9325892008297832\t1970-01-03T21:06:00.000000Z\n" +
                "UOJ\t0.019799837546746857\t1970-01-03T21:12:00.000000Z\n" +
                "JMY\t0.13660430775944932\t1970-01-03T21:18:00.000000Z\n" +
                "WVD\t0.046172168224761334\t1970-01-03T21:24:00.000000Z\n" +
                "OMV\t0.6067782579039555\t1970-01-03T21:30:00.000000Z\n" +
                "LPD\t0.1790475858715116\t1970-01-03T21:36:00.000000Z\n" +
                "UVS\t0.5613174142074612\t1970-01-03T21:42:00.000000Z\n" +
                "YHB\t0.10863061577000221\t1970-01-03T21:48:00.000000Z\n" +
                "OPJ\t0.5163028715917254\t1970-01-03T21:54:00.000000Z\n" +
                "YSS\t0.8245822920507528\t1970-01-03T22:00:00.000000Z\n" +
                "NZZ\t0.5681773238432949\t1970-01-03T22:06:00.000000Z\n" +
                "SJO\t0.04727174057972261\t1970-01-03T22:12:00.000000Z\n" +
                "OZZ\t0.7222951979294405\t1970-01-03T22:18:00.000000Z\n" +
                "ELL\t0.7951335970959716\t1970-01-03T22:24:00.000000Z\n" +
                "TJC\t0.7066431848881077\t1970-01-03T22:30:00.000000Z\n" +
                "FKW\t0.1810605823104886\t1970-01-03T22:36:00.000000Z\n" +
                "HZS\t0.839536764405929\t1970-01-03T22:42:00.000000Z\n" +
                "MSS\t0.8248550185892197\t1970-01-03T22:48:00.000000Z\n" +
                "OLY\t0.8669667625804924\t1970-01-03T22:54:00.000000Z\n" +
                "EHN\t0.8542659024913913\t1970-01-03T23:00:00.000000Z\n" +
                "OXP\t0.13027802162685043\t1970-01-03T23:06:00.000000Z\n" +
                "TGP\t0.9128848579835603\t1970-01-03T23:12:00.000000Z\n" +
                "HOL\t0.11594908641822632\t1970-01-03T23:18:00.000000Z\n" +
                "KJS\t0.9470551382496946\t1970-01-03T23:24:00.000000Z\n" +
                "RSZ\t0.3669999679163578\t1970-01-03T23:30:00.000000Z\n" +
                "SWH\t0.7234181773407536\t1970-01-03T23:36:00.000000Z\n" +
                "OMV\t0.844088760011128\t1970-01-03T23:42:00.000000Z\n" +
                "WCP\t0.7776474810620265\t1970-01-03T23:48:00.000000Z\n" +
                "WSW\t0.3074410595329138\t1970-01-03T23:54:00.000000Z\n" +
                "BRO\t0.9930633230891175\t1970-01-04T00:00:00.000000Z\n" +
                "SRY\t0.12027057950578746\t1970-01-04T00:06:00.000000Z\n" +
                "TGP\t0.440645592676968\t1970-01-04T00:12:00.000000Z\n" +
                "VFZ\t0.7436419445622273\t1970-01-04T00:18:00.000000Z\n" +
                "YRX\t0.49950663682485574\t1970-01-04T00:24:00.000000Z\n" +
                "VLT\t0.6600337469738781\t1970-01-04T00:30:00.000000Z\n" +
                "OQM\t0.060162223725059416\t1970-01-04T00:36:00.000000Z\n" +
                "JMY\t0.7573509485315202\t1970-01-04T00:42:00.000000Z\n" +
                "GZS\t0.04229155272030727\t1970-01-04T00:48:00.000000Z\n" +
                "OQM\t0.11500943478849246\t1970-01-04T00:54:00.000000Z\n" +
                "ZUL\t0.23290767295012593\t1970-01-04T01:00:00.000000Z\n" +
                "HNZ\t0.9582280075093402\t1970-01-04T01:06:00.000000Z\n" +
                "FLR\t0.22243351688740076\t1970-01-04T01:12:00.000000Z\n" +
                "ZLU\t0.11493568065083815\t1970-01-04T01:18:00.000000Z\n" +
                "KRG\t0.4058708191934428\t1970-01-04T01:24:00.000000Z\n" +
                "ZGH\t0.8503316000896455\t1970-01-04T01:30:00.000000Z\n" +
                "KRG\t0.45039214871547917\t1970-01-04T01:36:00.000000Z\n" +
                "VTM\t0.7593868458005614\t1970-01-04T01:42:00.000000Z\n" +
                "FYU\t0.9520909221021127\t1970-01-04T01:48:00.000000Z\n" +
                "MPG\t0.6385649970707139\t1970-01-04T01:54:00.000000Z\n" +
                "ZIM\t0.2524690658195553\t1970-01-04T02:00:00.000000Z\n" +
                "OMV\t0.2917796053045747\t1970-01-04T02:06:00.000000Z\n" +
                "XWC\t0.05339947229164044\t1970-01-04T02:12:00.000000Z\n" +
                "RFB\t0.9759588687260482\t1970-01-04T02:18:00.000000Z\n" +
                "OLY\t0.39303711474170466\t1970-01-04T02:24:00.000000Z\n" +
                "KJS\t0.21145554092946495\t1970-01-04T02:30:00.000000Z\n" +
                "OQM\t0.3619006645431899\t1970-01-04T02:36:00.000000Z\n" +
                "PEH\t0.19649587463949536\t1970-01-04T02:42:00.000000Z\n" +
                "OPJ\t0.625275838160094\t1970-01-04T02:48:00.000000Z\n" +
                "SED\t0.6926290975242375\t1970-01-04T02:54:00.000000Z\n" +
                "YPH\t0.5354981873381928\t1970-01-04T03:00:00.000000Z\n" +
                "HGO\t0.9425658579916102\t1970-01-04T03:06:00.000000Z\n" +
                "VTJ\t0.08050733448427783\t1970-01-04T03:12:00.000000Z\n" +
                "KJS\t0.6406201315963813\t1970-01-04T03:18:00.000000Z\n" +
                "PEH\t0.9028381160965113\t1970-01-04T03:24:00.000000Z\n" +
                "HZE\t0.6953408763887957\t1970-01-04T03:30:00.000000Z\n" +
                "ETJ\t0.654226248740447\t1970-01-04T03:36:00.000000Z\n" +
                "KFL\t0.8706500807260904\t1970-01-04T03:42:00.000000Z\n" +
                "MBE\t0.8495405342675554\t1970-01-04T03:48:00.000000Z\n" +
                "UQS\t0.9633967086060156\t1970-01-04T03:54:00.000000Z\n" +
                "GZS\t0.45384900266810624\t1970-01-04T04:00:00.000000Z\n" +
                "TGP\t0.18563835622052482\t1970-01-04T04:06:00.000000Z\n" +
                "FKW\t0.41716226532623835\t1970-01-04T04:12:00.000000Z\n" +
                "HFV\t0.6459182763886085\t1970-01-04T04:18:00.000000Z\n" +
                "HNZ\t0.3122920574163822\t1970-01-04T04:24:00.000000Z\n" +
                "SRY\t0.22182782808079837\t1970-01-04T04:30:00.000000Z\n" +
                "GHV\t0.2763714033301786\t1970-01-04T04:36:00.000000Z\n" +
                "FOW\t0.8370647121475187\t1970-01-04T04:42:00.000000Z\n" +
                "HFV\t0.3948400410427655\t1970-01-04T04:48:00.000000Z\n" +
                "NRX\t0.11465885305674761\t1970-01-04T04:54:00.000000Z\n" +
                "OON\t0.35890489392104297\t1970-01-04T05:00:00.000000Z\n" +
                "HML\t0.2977278793266547\t1970-01-04T05:06:00.000000Z\n" +
                "KKH\t0.21535412791353414\t1970-01-04T05:12:00.000000Z\n" +
                "XUX\t0.04984237052677154\t1970-01-04T05:18:00.000000Z\n" +
                "FLR\t0.05059835909941457\t1970-01-04T05:24:00.000000Z\n" +
                "HFV\t0.2582028171564791\t1970-01-04T05:30:00.000000Z\n" +
                "SRY\t0.1296510741209903\t1970-01-04T05:36:00.000000Z\n" +
                "MPG\t0.5758275756074844\t1970-01-04T05:42:00.000000Z\n" +
                "HML\t0.7692964421576207\t1970-01-04T05:48:00.000000Z\n" +
                "WSW\t0.5621120081615097\t1970-01-04T05:54:00.000000Z\n";

        assertQuery(
                "k\tprice\tts\n",
                "select sym k, price, ts from x where sym != 'HBC'",
                "create table x (\n" +
                        "    sym symbol cache index,\n" +
                        "    price double,\n" +
                        "    ts timestamp\n" +
                        ") timestamp(ts) partition by DAY",
                "ts",
                "insert into x select * from (select rnd_symbol(120, 3, 3, 0) sym, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(172800000000, 360000000) ts \n" +
                        "    from long_sequence(300)) timestamp (ts)",
                expected,
                true,
                true,
                false,
                true
        );
    }

    @Test
    public void testSymbolNotPresentAndThenInserted() throws Exception {
        final String expected = "k\tprice\tts\n";

        String query = "select sym k, price, ts from x where sym = 'ABC'";
        assertQuery(
                "k\tprice\tts\n",
                query,
                "create table x as (" +
                        "select " +
                        "rnd_symbol('ABB', 'HBC', 'DXR') sym, " +
                        "rnd_double() price, " +
                        "timestamp_sequence(172800000000, 360000000) ts " +
                        "from long_sequence(10)" +
                        ") timestamp(ts) partition by DAY",
                "ts",
                "insert into x select * from (select rnd_symbol('ABC') sym, \n" +
                        "        rnd_double() price, \n" +
                        "        timestamp_sequence(177800000000, 360000000) ts \n" +
                        "    from long_sequence(2)) timestamp (ts)",
                expected +
                        "ABC\t0.6276954028373309\t1970-01-03T01:23:20.000000Z\n" +
                        "ABC\t0.6778564558839208\t1970-01-03T01:29:20.000000Z\n",
                true
        );
    }
}
