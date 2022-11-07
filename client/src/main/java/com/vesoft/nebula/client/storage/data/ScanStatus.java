/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.data;

public enum ScanStatus {
    /** all parts succeed */
    ALL_SUCCESS,

    /** part of parts succeed */
    PART_SUCCESS;
}
