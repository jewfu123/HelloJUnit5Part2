-- Copyright 2017 Makoto Consulting Group, Inc.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
CREATE TABLE hju5_person(
  id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  when_created DATE NOT NULL DEFAULT CURRENT_DATE,
  when_last_updated DATE NOT NULL DEFAULT CURRENT_DATE,
  last_name VARCHAR(64) NOT NULL,
  first_name VARCHAR(64) NOT NULL,
  age INTEGER NOT NULL,
  eye_color VARCHAR(16) NOT NULL,
  gender VARCHAR(16) NOT NULL
  );

ALTER TABLE hju5_person ADD CONSTRAINT uc_1 UNIQUE(last_name, first_name);
  