package com.ubirch.user.client.formats

import com.ubirch.user.client.model.Action
import org.json4s.{CustomSerializer, JString, MappingException}

object UserFormats {

  def createStringFormat[A: Manifest](
                                       decode: String => A,
                                       validation: String => Boolean)(encode: A => String): CustomSerializer[A] = {
    val Class = implicitly[Manifest[A]].runtimeClass
    new CustomSerializer[A](_ =>
      (
        {
          case JString(value) if validation(value) => decode(value)
          case JString(_)                          => throw new MappingException("Can't convert value to " + Class)
        },
        {
          case a: A => JString(encode(a))
        }
      ))
  }

  private val actionTypeFormat: CustomSerializer[Action] =
    createStringFormat(Action.unsafeFromString, _.nonEmpty)(Action.toFormattedString)
  val all = List(actionTypeFormat)

}