package com.mep.config

import com.mep.dao.FootprintDaoComponent

class ComponentProvider extends FootprintDaoComponent {
  override lazy val footprintDao: FootprintDao = new FootprintDao
}
