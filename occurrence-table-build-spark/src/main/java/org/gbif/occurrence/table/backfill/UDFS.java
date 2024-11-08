/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.occurrence.table.backfill;

import org.gbif.occurrence.table.udf.CleanDelimiterArraysUdf;
import org.gbif.occurrence.table.udf.CleanDelimiterCharsUdf;
import org.gbif.occurrence.table.udf.StringArrayContainsGenericUdf;
import org.gbif.occurrence.table.udf.ToISO8601Udf;
import org.gbif.occurrence.table.udf.ToLocalISO8601Udf;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UDFS {

  public static void registerUdfs(SparkSession sparkSession) {
    sparkSession.udf().register("cleanDelimiters", new CleanDelimiterCharsUdf(), DataTypes.StringType);
    sparkSession.udf().register("cleanDelimitersArray", new CleanDelimiterArraysUdf(), DataTypes.createArrayType(DataTypes.StringType));
    sparkSession.udf().register("toISO8601", new ToISO8601Udf(), DataTypes.StringType);
    sparkSession.udf().register("toLocalISO8601", new ToLocalISO8601Udf(), DataTypes.StringType);
    sparkSession.udf().register("stringArrayContains", new StringArrayContainsGenericUdf(), DataTypes.BooleanType);
  }

}
