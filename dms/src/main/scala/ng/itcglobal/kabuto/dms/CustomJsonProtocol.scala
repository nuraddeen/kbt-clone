package ng.itcglobal.kabuto
package dms

import java.util.UUID
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
 
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.json.DeserializationException
import spray.json.JsString
import spray.json.JsValue
import spray.json.RootJsonFormat

import ng.itcglobal.kabuto._
import core.util.Enum.HttpResponseStatus
import core.util.Util.KabutoApiHttpResponse


import FileManagerService.Application
import ng.itcglobal.kabuto.core.db.postgres.Tables

trait CustomJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  import JobRepository._

  implicit object StatusFormat extends RootJsonFormat[Status] {
    def write(status: Status): JsValue = status match {
      case Failed     => JsString("Failed")
      case Successful => JsString("Successful")
    }

    def read(json: JsValue): Status = json match {
      case JsString("Failed")     => Failed
      case JsString("Successful") => Successful
      case _                      => throw new DeserializationException("Status unexpected")
    }
  }


   implicit object UUIDFormat extends RootJsonFormat[UUID] {
    def write(obj: UUID): JsValue = JsString(obj.toString())

     def read(json: JsValue): UUID = json match {
      case JsString(str) => UUID.fromString(str)
      case _ => throw new DeserializationException("invalid UUID string")
    }
  }

  implicit object DateJsonFormat extends RootJsonFormat[LocalDateTime] {

    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    override def write(obj: LocalDateTime)          = JsString(obj.format(formatter))
    override def read(json: JsValue): LocalDateTime = json match {
      case JsString(s) => LocalDateTime.parse(s, formatter)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }

  implicit val jobFormat = jsonFormat4(Job)
  implicit val appFormat = jsonFormat2(Application)
  implicit val docDtoFormat = jsonFormat7(DocumentProcessorService.DocumentDto)
  implicit val docMetadataFormat = jsonFormat7(DocumentProcessorService.DocumentMetaDataPayload)



   implicit object HttpResponseStatusFormat extends RootJsonFormat[HttpResponseStatus.Value] {

    def write(status: HttpResponseStatus.Value): JsValue =
      status match {
        case HttpResponseStatus.NotFound              => JsString("Not Found") 
        case HttpResponseStatus.NotSupported          => JsString("Not Supported") 
        case HttpResponseStatus.NotAllowed            => JsString("Not Allowed")
        case HttpResponseStatus.DuplicateRequest      => JsString("Duplicate Request")
        case HttpResponseStatus.Success               => JsString("Success")
        case HttpResponseStatus.Failed                => JsString("Failed") 
      }

    def read(json: JsValue): HttpResponseStatus.Value =
      json match {
        case JsString("Not Found")                =>  HttpResponseStatus.NotFound
        case JsString("Not Supported")            =>  HttpResponseStatus.NotSupported
        case JsString("Not Allowed")              =>  HttpResponseStatus.NotAllowed
        case JsString("Duplicate Request")        =>  HttpResponseStatus.DuplicateRequest
        case JsString("Success")                  =>  HttpResponseStatus.Success 
        case JsString("Failed")                   =>  HttpResponseStatus.Failed
        case _                                    =>
          throw new DeserializationException("Status unexpected")
      }

  }

  implicit val httpResponse     = jsonFormat4(KabutoApiHttpResponse)

}