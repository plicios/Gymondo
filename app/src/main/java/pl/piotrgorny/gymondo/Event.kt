package pl.piotrgorny.gymondo

import pl.piotrgorny.gymondo.data.model.Exercise

open class Event

class ShowExerciseDetailsEvent(val exercise: Exercise) : Event()
class ShowApiErrorEvent(val errorText: String) : Event()