/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import com.vesoft.nebula.proto.graph.NestedVector;
import com.vesoft.nebula.proto.graph.VectorBatch;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Vector Batch to maintain the VectorBatch and its vectors
 */
public class Batch {
    private final VectorBatch         batch;
    private final List<VectorWrapper> vectors = new ArrayList<>();

    public Batch(VectorBatch batch, ByteOrder byteOrder) {
        this.batch = batch;
        for (NestedVector vector : batch.getVectorsList()) {
            vectors.add(new VectorWrapper(vector, byteOrder));
        }
    }


    /**
     * get count of vectors in this Batch
     *
     * @return count of vectors
     */
    public int getVectorsCount() {
        return batch.getVectorsCount();
    }


    /**
     * get the VectorWrapper with specific index of the batch
     *
     * @param index index
     * @return VectorWrapper
     */
    public VectorWrapper getVectors(int index) {
        return vectors.get(index);
    }

    /**
     * get the row size of this batch
     *
     * @return row size
     */
    public int getBatchRowSize() {
        if (getVectorsCount() > 0) {
            return batch.getVectors(0).getCommonMetaData().getNumRecords();
        } else {
            return 0;
        }
    }

}
