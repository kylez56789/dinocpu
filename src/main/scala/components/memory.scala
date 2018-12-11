// The instruction and data memory modules

package CODCPU

import chisel3._

import chisel3.util.experimental.loadMemoryFromFile

/**
 * Describe this
 */
class IMemIO extends Bundle {
  val address     = Input(UInt(32.W))

  val instruction = Output(UInt(32.W))
}

/**
 * Describe this
 */
class DMemIO extends Bundle {
  val address   = Input(UInt(32.W))
  val writedata = Input(UInt(32.W))
  val memread   = Input(Bool())
  val memwrite  = Input(Bool())

  val readdata  = Output(UInt(32.W))
}

class DualPortedMemory(size: Int, memfile: String) extends Module {
  val io = IO(new Bundle {
    val imem = new IMemIO
    val dmem = new DMemIO
  })
  io := DontCare

  val memory = Mem(math.ceil(size.toDouble/4).toInt, UInt(32.W))
  loadMemoryFromFile(memory, memfile)

  // The instruction memory side
  assert(io.imem.address < size.U, "Cannot access outside of memory bounds")
  assert(!(io.imem.address & 3.U), "Cannot do unaligned accesses to memory")

  io.imem.instruction := memory(io.imem.address)

  // The data memory side
  assert(io.dmem.address < size.U, "Cannot access outside of memory bounds")
  assert(!(io.dmem.address & 3.U), "Cannot do unaligned accesses to memory")

  when (io.dmem.memread) {
    io.dmem.readdata := memory(io.dmem.address)
  }

  when (io.dmem.memwrite) {
    memory(io.dmem.address) := io.dmem.writedata
  }
}
