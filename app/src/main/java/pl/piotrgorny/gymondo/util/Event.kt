package pl.piotrgorny.gymondo.util

import pl.piotrgorny.gymondo.data.model.Exercise

abstract class Event

class ShowExerciseDetailsEvent(val exercise: Exercise) : Event()
class ShowApiErrorEvent(val errorText: String) : Event()
class LoadingNextPage : Event()
class FinishedLoadingNextPage : Event()