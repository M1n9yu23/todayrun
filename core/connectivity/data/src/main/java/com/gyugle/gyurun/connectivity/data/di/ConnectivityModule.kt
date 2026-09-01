package com.gyugle.gyurun.connectivity.data.di

import com.gyugle.gyurun.connectivity.data.WearNodeDiscovery
import com.gyugle.gyurun.connectivity.data.messaging.WearMessagingClient
import com.gyugle.gyurun.core.connectivity.domain.NodeDiscovery
import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingClient
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val connectivityModule =
    module {
        singleOf(::WearMessagingClient) { bind<MessagingClient>() }
        singleOf(::WearNodeDiscovery) { bind<NodeDiscovery>() }
    }
