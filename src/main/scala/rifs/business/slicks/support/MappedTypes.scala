/*
 * Copyright (C) 2016  Department for Business, Energy and Industrial Strategy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rifs.business.slicks.support

import com.wellfactored.valuewrapper.{ValueWrapper, ValueWrapperGen}

import scala.reflect.ClassTag

trait MappedTypes extends ValueWrapperGen {
  self: DBBinding =>

  import driver.api._

  implicit def wrapperMapper[W, V](implicit vw: ValueWrapper[W, V], vMapper: ColumnType[V], tag: ClassTag[W]): ColumnType[W] = {
    def tmap(w: W): V = vw.unwrap(w)

    // TODO: improve the error handling on this to remove the naked get
    def tcomap(v: V): W = vw.wrap(v).right.get

    MappedColumnType.base[W, V](tmap, tcomap)
  }
}
