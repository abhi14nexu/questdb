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

package io.questdb.cutlass.text;

import io.questdb.std.Mutable;

public class TextImportRequestTask implements Mutable {
    private int atomicity;
    private byte delimiter;
    private String fileName;
    private boolean headerFlag;
    private long importId;
    private int partitionBy;
    private String tableName;
    private String timestampColumnName;
    private String timestampFormat;

    @Override
    public void clear() {
        this.importId = -1;
        this.tableName = null;
        this.fileName = null;
        this.headerFlag = false;
        this.timestampColumnName = null;
        this.delimiter = 0;
        this.timestampFormat = null;
        this.partitionBy = -1;
        this.atomicity = -1;
    }

    public int getAtomicity() {
        return atomicity;
    }

    public byte getDelimiter() {
        return delimiter;
    }

    public String getFileName() {
        return fileName;
    }

    public long getImportId() {
        return importId;
    }

    public int getPartitionBy() {
        return partitionBy;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTimestampColumnName() {
        return timestampColumnName;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public boolean isHeaderFlag() {
        return headerFlag;
    }

    public void of(
            long importId,
            String tableName,
            String fileName,
            boolean headerFlag,
            String timestampColumnName,
            byte delimiter,
            String timestampFormat,
            int partitionBy,
            int atomicity
    ) {
        this.clear();
        this.importId = importId;
        this.tableName = tableName;
        this.fileName = fileName;
        this.headerFlag = headerFlag;
        this.timestampColumnName = timestampColumnName;
        this.delimiter = delimiter;
        this.timestampFormat = timestampFormat;
        this.partitionBy = partitionBy;
        this.atomicity = atomicity;
    }
}
