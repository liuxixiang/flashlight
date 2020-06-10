package com.mysosflash.light.base

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment {
    constructor(contentLayoutId: Int) : super(contentLayoutId)
}