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
