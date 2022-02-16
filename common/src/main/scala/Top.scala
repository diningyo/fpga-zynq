package zynq

import chisel3._
import freechips.rocketchip.config.{Parameters, Field}
import freechips.rocketchip.devices.tilelink._
import freechips.rocketchip.diplomacy.{LazyModule, LazyModuleImp}
import freechips.rocketchip.subsystem._
import freechips.rocketchip.util.DontTouch
import freechips.rocketchip.devices.debug.Debug
import testchipip._

case object ZynqAdapterBase extends Field[BigInt]

class Top(implicit val p: Parameters) extends Module {
  val address = p(ZynqAdapterBase)
  val config = p(ExtIn)
  val ltarget = LazyModule(new FPGAZynqTop)
  val target = Module(ltarget.module)
  val adapter = Module(LazyModule(new ZynqAdapter(address, config)).module)

  require(ltarget.mem_axi4.size == 1)

  val io = IO(new Bundle {
    val ps_axi_slave = Flipped(adapter.axi.cloneType)
    val mem_axi = ltarget.mem_axi4.head.cloneType
  })

  io.mem_axi <> ltarget.mem_axi4.head
  adapter.axi <> io.ps_axi_slave
  adapter.io.serial <> target.serial.get
  adapter.io.bdev <> target.bdev.get

  Debug.tieoffDebug(target.debug, target.resetctrl, Some(target.psd))
  target.debug.get.dmactiveAck := false.B
  target.debug.get.clockeddmi.get.dmi.req := DontCare
  //target.debug.get := DontCare
  target.tieOffInterrupts()
  target.dontTouchPorts()
  target.reset := adapter.io.sys_reset
}

class FPGAZynqTop(implicit p: Parameters) extends RocketSubsystem
    with CanHaveMasterAXI4MemPort
    with HasAsyncExtInterrupts
    with CanHavePeripherySerial
    with CanHavePeripheryBlockDevice {

  // optionally add ROM devices
  // Note that setting BootROMLocated will override the reset_vector for all tiles
  val bootROM  = p(BootROMLocated(location)).map { BootROM.attach(_, this, CBUS) }
  val maskROMs = p(MaskROMLocated(location)).map { MaskROM.attach(_, this, CBUS) }

  override lazy val module = new FPGAZynqTopModule(this)
}

class FPGAZynqTopModule(outer: FPGAZynqTop) extends RocketSubsystemModuleImp(outer)
    with HasRTCModuleImp
    with HasExtInterruptsModuleImp
    with CanHavePeripherySerialModuleImp
    with CanHavePeripheryBlockDeviceModuleImp
    with DontTouch
