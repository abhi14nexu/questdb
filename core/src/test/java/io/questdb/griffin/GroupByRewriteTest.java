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

public class GroupByRewriteTest extends AbstractGriffinTest {

    @Test
    public void testSumOfAddition1() throws Exception {
        assertAggQuery("r\n" +
                        "65\n",
                "select sum(x+1) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfAddition2() throws Exception {
        assertAggQuery("r\n" +
                        "65\n",
                "select sum(1+x) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfAdditionOfDouble1() throws Exception {
        assertAggQuery("r\n" +
                        "66.0\n",
                "select sum(d+1) r from y",
                "create table y as ( select x + 0.1d as d from long_sequence(10) )"
        );
    }

    @Test//all values except first are Infinity and thus ignored
    public void testSumOfAdditionOfDouble2() throws Exception {
        assertAggQuery("r\n" +
                        "1.7E308\n",
                "select sum(d+1) r from y",
                "create table y as ( select 1.7E308 * x as d  from long_sequence(10) )"
        );
    }

    @Test//all values except first are NaN and thus ignored
    public void testSumOfAdditionOfDouble3() throws Exception {
        assertAggQuery("r\n" +
                        "2.0\n",
                "select sum(d+1) r from y",
                "create table y as ( select (1.7E308 * x)/(1.7E308*x) as d  from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfAdditionOverflow1() throws Exception {
        assertAggQuery("r\n" +
                        "-9223372036854775805\n",
                "select sum(x+9223372036854775807) r from y",
                "create table y as ( select x from long_sequence(3) )"
        );
    }

    @Test
    public void testSumOfAdditionOverflow2() throws Exception {
        assertAggQuery("r\n" +
                        "-9223372036854775805\n",
                "select sum(x) + 9223372036854775807*3 r from y",
                "create table y as ( select x from long_sequence(3) )"
        );
    }

    @Test
    public void testSumOfAdditionWithNull() throws Exception {
        assertAggQuery("r\n" +
                        "NaN\n",
                "select sum(x+null) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );

        assertAggQuery("r\n" +
                        "NaN\n",
                "select sum(null+x) r from y",
                null
        );
    }

    //multiplication
    @Test
    public void testSumOfMultiplication1() throws Exception {
        assertAggQuery("r\n" +
                        "55\n",
                "select sum(x*1) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfMultiplication2() throws Exception {
        assertAggQuery("r\n" +
                        "55\n",
                "select sum(1*x) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfMultiplicationOfDouble1() throws Exception {
        assertAggQuery("r\n" +
                        "112.00000000000001\n",
                "select sum(d*2) r from y",
                "create table y as ( select x + 0.1d as d from long_sequence(10) )"
        );
    }

    @Test//all values except first are Infinity and thus ignored
    public void testSumOfMultiplicationOfDouble2() throws Exception {
        assertAggQuery("r\n" +
                        "1.7E308\n",
                "select sum(d*2) r from y",
                "create table y as ( select (1.7E308/2)*x as d  from long_sequence(10) )"
        );
    }

    @Test//all values except first are NaN and thus ignored
    public void testSumOfMultiplicationOfDouble3() throws Exception {
        assertAggQuery("r\n" +
                        "2.0\n",
                "select sum(d*2) r from y",
                "create table y as ( select (1.7E308 * x)/(1.7E308*x) as d  from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfMultiplicationOverflow1() throws Exception {
        assertAggQuery("r\n" +
                        "-6\n",
                "select sum(x*9223372036854775807) r from y",
                "create table y as ( select x from long_sequence(3) )"
        );
    }

    @Test
    public void testSumOfMultiplicationOverflow2() throws Exception {
        assertAggQuery("r\n" +
                        "-6\n",
                "select sum(x) * 9223372036854775807 r from y",
                "create table y as ( select x from long_sequence(3) )"
        );
    }

    @Test
    public void testSumOfMultiplicationWithNull() throws Exception {
        assertAggQuery("r\n" +
                        "NaN\n",
                "select sum(x*null) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );

        assertAggQuery("r\n" +
                        "NaN\n",
                "select sum(null*x) r from y",
                null
        );
    }

    //subtraction
    @Test
    public void testSumOfSubtraction1() throws Exception {
        assertAggQuery("r\n" +
                        "45\n",
                "select sum(x-1) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfSubtraction2() throws Exception {
        assertAggQuery("r\n" +
                        "-45\n",
                "select sum(1-x) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfSubtractionOfDouble1() throws Exception {
        assertAggQuery("r\n" +
                        "46.0\n",
                "select sum(d-1) r from y",
                "create table y as ( select x + 0.1d as d from long_sequence(10) )"
        );
    }

    @Test//all values except first are Infinity and thus ignored
    public void testSumOfSubtractionOfDouble2() throws Exception {
        assertAggQuery("r\n" +
                        "-1.7E308\n",
                "select sum(d-1) r from y",
                "create table y as ( select -1.7E308 * x as d  from long_sequence(10) )"
        );
    }

    @Test//all values except first are NaN and thus ignored
    public void testSumOfSubtractionOfDouble3() throws Exception {
        assertAggQuery("r\n" +
                        "2.0\n",
                "select sum(d+1) r from y",
                "create table y as ( select (1.7E308 * x)/(1.7E308*x) as d  from long_sequence(10) )"
        );
    }

    @Test
    public void testSumOfSubtractionOverflow1() throws Exception {
        assertAggQuery("r\n" +
                        "9223372036854775805\n",
                "select sum(x-9223372036854775807) r from y",
                "create table y as ( select -x x from long_sequence(3) )"
        );
    }

    @Test
    public void testSumOfSubtractionOverflow2() throws Exception {
        assertAggQuery("r\n" +
                        "9223372036854775805\n",
                "select sum(x) - 9223372036854775807*3 r from y",
                "create table y as ( select -x x from long_sequence(3) )"
        );
    }

    @Test
    public void testSumOfSubtractionWithNull() throws Exception {
        assertAggQuery("r\n" +
                        "NaN\n",
                "select sum(x-null) r from y",
                "create table y as ( select x from long_sequence(10) )"
        );

        assertAggQuery("r\n" +
                        "NaN\n",
                "select sum(null-x) r from y",
                null
        );
    }


    private void assertAggQuery(String expected,
                                String query,
                                String ddl) throws Exception {
        assertQuery(expected,
                query,
                ddl,
                null,
                false,
                true,
                true
        );
    }

}
