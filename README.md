# Someguy's RISC-V Core (RV32IMAFV+bf16) 

A relatively "simple" synthesizable, **5-stage pipelined RISC-V processor** written in **Scala/Chisel**. Implements the **32-bit RISC-V ISA** with support for:

- Integer operations
- Hardware multiplication/division
- Atomics
- Single-precision floating point
- Vector operations
- **BFloat16 (bf16)** machine learning extensions

Designed for **FPGA deployment** (Artix-7 / Zybo) and **Verilator simulation**, featuring a dynamic **Branch Target Buffer (BTB)** and **Memory-Mapped I/O (UART)**.

---

## Architecture Overview

The core follows a classic **5-stage pipeline**:

- Fetch
- Decode
- Execute
- Memory
- Writeback

It is augmented with parallel execution units for **Floating Point** and **Vector** operations.

**Key specs:**

- **ISA:** `RV32IMAFV_Zbf16`
- **Pipeline Depth:** 5 stages (Integer), variable latency (Float/Vector)
- **Branch Prediction:** 64-entry direct-mapped BTB with 2-bit saturating counters
- **Bus Interface:** Simple instruction/data memory interface (adaptable to Wishbone)

---

## ISA Extensions Breakdown

### 1) Base Integer (I)

- Full support for **RV32I**
- Excludes `ECALL`, `EBREAK`, and `FENCE`
- **Hazard Handling:**
  - Full forwarding unit (EX→EX, MEM→EX)
  - Hazard detection unit for load-use stalls

### 2) Multiply/Divide (M)

- **Hardware Multiplier:** 2-cycle latency pipelined multiplier
  - `mul`, `mulh`, `mulhsu`, `mulhu`
- **Hardware Divider:** iterative radix-2 non-restoring divider (variable latency)

### 3) Atomics (A)

- Supports `LR.W` (Load Reserved) and `SC.W` (Store Conditional)
- Atomic Memory Operations (AMO):
  - `amoswap`, `amoadd`, `amoand`, `amoxor`, `amomax`
- **Implementation note:** implemented inside the Load/Store Unit (LSU) to lock memory access during atomic sequences

### 4) Single-Precision Floating Point (F)

- Separate 32-entry FP register file: `f0`–`f31`
- IEEE 754-2008 compliant
- Units:
  - Adder
  - Multiplier
  - FMA (Fused Multiply-Add)
  - Sqrt/Div unit
- Rounding modes: supports Dynamic Rounding Mode (`dyn`) via `fcsr` 

### 5) Vector Extension (V)

- **VLEN:** configurable (default: 128 bits)
- **Lanes:** 2 parallel vector lanes
- Features: chaining, masking, and strided loads/stores
- Supports `vsetvli` for dynamic vector length configuration

### 6) BFloat16 (bf16)

Designed for AI/ML acceleration with support for “Brain Floating Point” format:

- **Format:** 1 sign bit, 8 exponent bits, 7 mantissa bits (matches FP32 dynamic range)
- **Instructions:**
  - `vfadd.bf16` — Vector BFloat16 Add
  - `vfmul.bf16` — Vector BFloat16 Multiply
  - `vfmacc.bf16` — Vector BFloat16 Multiply-Accumulate
- **Storage:** bf16 values are packed into standard F-registers or V-registers (2 per 32-bit slot)
