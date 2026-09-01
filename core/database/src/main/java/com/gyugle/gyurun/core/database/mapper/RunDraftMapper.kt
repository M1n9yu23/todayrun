package com.gyugle.gyurun.core.database.mapper

import com.gyugle.gyurun.core.database.entity.RunDraftEntity
import com.gyugle.gyurun.core.domain.run.RunDraft
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

internal fun RunDraftEntity.toRunDraft(): RunDraft =
    RunDraft(
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        duration = durationMillis.milliseconds,
        distanceMeters = distanceMeters,
        route = route.toRoute(),
        steps = steps,
    )

internal fun RunDraft.toRunDraftEntity(): RunDraftEntity =
    RunDraftEntity(
        dateTimeUtc = dateTimeUtc.toInstant().toString(),
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        route = route.toRouteJson(),
        steps = steps,
    )
