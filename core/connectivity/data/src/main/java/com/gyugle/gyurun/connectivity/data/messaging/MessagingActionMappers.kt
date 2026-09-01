package com.gyugle.gyurun.connectivity.data.messaging

import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction

internal fun MessagingAction.toMessagingActionDto(): MessagingActionDto =
    when (this) {
        MessagingAction.StartOrResume -> MessagingActionDto.StartOrResume
        MessagingAction.Pause -> MessagingActionDto.Pause
        MessagingAction.Finish -> MessagingActionDto.Finish
        MessagingAction.ConnectionRequest -> MessagingActionDto.ConnectionRequest
        MessagingAction.Trackable -> MessagingActionDto.Trackable
        MessagingAction.Untrackable -> MessagingActionDto.Untrackable
        is MessagingAction.HeartRateUpdate -> MessagingActionDto.HeartRateUpdate(heartRate)
        is MessagingAction.StepCountUpdate -> MessagingActionDto.StepCountUpdate(stepCount)
        is MessagingAction.DistanceUpdate -> MessagingActionDto.DistanceUpdate(distanceMeters)
        is MessagingAction.TimeUpdate -> MessagingActionDto.TimeUpdate(elapsedDuration)
    }

internal fun MessagingActionDto.toMessagingAction(): MessagingAction =
    when (this) {
        MessagingActionDto.StartOrResume -> MessagingAction.StartOrResume
        MessagingActionDto.Pause -> MessagingAction.Pause
        MessagingActionDto.Finish -> MessagingAction.Finish
        MessagingActionDto.ConnectionRequest -> MessagingAction.ConnectionRequest
        MessagingActionDto.Trackable -> MessagingAction.Trackable
        MessagingActionDto.Untrackable -> MessagingAction.Untrackable
        is MessagingActionDto.HeartRateUpdate -> MessagingAction.HeartRateUpdate(heartRate)
        is MessagingActionDto.StepCountUpdate -> MessagingAction.StepCountUpdate(stepCount)
        is MessagingActionDto.DistanceUpdate -> MessagingAction.DistanceUpdate(distanceMeters)
        is MessagingActionDto.TimeUpdate -> MessagingAction.TimeUpdate(elapsedDuration)
    }
