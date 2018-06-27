#!/usr/bin/env python
# _*_ coding: utf-8 _*_
import time
import os
import subprocess
import glob
import shutil
import networkx as nx
import angr
from sys import argv
# from angrutils import plot_cfg, plot_dfg
# import matplotlib.pyplot as plt
import sys

# sys.setrecursionlimit(1000000)加大递归深度结果差别不大,但程序会中断

dir = os.getcwd()  # 工程目录
bin_dir = dir + os.sep + "binary"
fea_dir = dir + os.sep + "feature"
programlist = ["copyfirmware"]  # ["openssl","busybox","coreutils"]#需要抽取的工程
bin_num = 0
func_num = 0
function_list_file = ""
function_list_fp = None

'''
def getAllFunctionAddrs(cfg):
    addr_name_dict = {}
    someLibs = ['_init','_start','__x86.get_pc_thunk.bx','deregister_tm_clones','__do_global_dtors_aux','frame_dummy','__libc_csu_init','printf','__libc_start_main','__libc_start_main']
    for addr, func in cfg.kb.functions.iteritems():
        #print hex(addr), func.name, func.is_plt, func.is_syscall
        if not func.is_plt:
            if func.name not in someLibs:
                addr_name_dict[addr] = func.name                
    return addr_name_dict
'''


# 识别出二进制中所有的函数名和开始地址，以地址：函数名的形式返回字典
def getAllFunctionAddrs(p):
    print
    "allfunctions:", p.loader.main_object._symbols_by_name
    addr_name_dict = {}
    for name in p.loader.main_object._symbols_by_name:
        tempSymbol = p.loader.main_object.get_symbol(name)
        if tempSymbol.is_function and not tempSymbol.is_import:
            print
            "tempSymbol:", name, hex(tempSymbol.rebased_addr)
            if tempSymbol.is_import:
                print
                "imported:", name, hex(tempSymbol.rebased_addr)
            addr_name_dict[tempSymbol.rebased_addr] = name
    return addr_name_dict


def getStart_End_Addrs(cfg, addrs_names_dict, proj):
    allFunctionsStartAndEnd = {}
    deletedFunctionAddrs = []  # exception during get start and end address
    for item in addrs_names_dict.keys():
        # print "========================================="
        # print "addr:",item,type(item),hex(item),"name:",addrs_names_dict[item]
        blocks_addrs = []
        block_addr_object_dict = {}
        max_block_addr = 0
        max_block = None
        try:
            blocks = cfg.kb.functions[item].blocks  # ca.o中的几个函数在这条语句抛异常，所以不为他们生成cfg和dfg
            # print "current node:", hex(block.addr)
        except:
            deletedFunctionAddrs.append(item)
            continue
        else:
            for block in blocks:
                if block.addr > max_block_addr:
                    max_block_addr = block.addr
                    max_block = block
                    # instruction_addrs = sorted(proj.factory.block(max_block_addr).instruction_addrs)
            instruction_addrs = sorted(max_block.instruction_addrs)
            allFunctionsStartAndEnd[hex(item)] = hex(instruction_addrs[-1])
    print
    "AllFunctionsStartAndEnd:", allFunctionsStartAndEnd
    return allFunctionsStartAndEnd, deletedFunctionAddrs


def extract_bin_fea(program, version, project, path):
    global bin_num, func_num, function_list_fp

    #  加载二进制文件
    p = angr.Project(project, load_options={'auto_load_libs': False})
    cfg = p.analyses.CFGFast(collect_data_references=False, resolve_indirect_jumps=False, force_complete_scan=False)
    addrs_names_dict = getAllFunctionAddrs(p)
    # print "old addrs_names:",addrs_names_dict
    allFunctionsStartAndEnd, deletedFunctionAddrs = getStart_End_Addrs(cfg, addrs_names_dict, p)
    for addr in deletedFunctionAddrs:  # 从所有地址：函数字典中删除无法识别函数边界的函数
        if addrs_names_dict.has_key(addr):
            addrs_names_dict.pop(addr)
    # print "new addrs_names:",addrs_names_dict
    names_addrs_dict = dict((v, k) for k, v in addrs_names_dict.iteritems())
    # print "names_addrs_dict:",names_addrs_dict
    print
    p.filename  # 文件的全名
    print
    p.arch  # 架构
    # cfg_all = p.analyses.CFGAccurate(normalize=True,keep_state = True)
    # 找到所有函数
    for functionName in names_addrs_dict.keys():
        print
        "functionName:", functionName
        # for functionName in p.loader.main_object._symbols_by_name:
        symbol = p.loader.main_object.get_symbol(functionName)
        # 位于.text中的函数
        if symbol.is_function:

            if os.path.exists(path + os.sep + functionName + "_cfg.txt"):
                continue

            func_num = func_num + 1

            # 生成控制流图
            start = time.time()
            # gaojian annotation
            # cfg = p.analyses.CFGAccurate(normalize=True, keep_state=True,starts=[symbol.rebased_addr])
            # gaojian add
            # cfg = p.analyses.CFGFast(collect_data_references=False,resolve_indirect_jumps=False,force_complete_scan=False, function_starts=[names_addrs_dict[functionName]])

            cfgtime = time.time()

            # 遍历所有的基本块,处理基本块内的反汇编代码,抽取基本块特征向量
            # block_fea(p.filename,functionName,p,cfg,path)

            # 遍历控制流边
            # cfg_edge_num = gen_cfg(p.filename,functionName,cfg,path,symbol.rebased_addr)
            cfg_edge_num = gen_cfg(p.filename, functionName, cfg, path, names_addrs_dict[functionName],
                                   allFunctionsStartAndEnd[hex(names_addrs_dict[functionName])])

            dfgtime = time.time()

            # 生成数据流图
            dfg_node_num, dfg_edge_num = gen_dfg(cfg_edge_num, functionName, cfg, symbol.rebased_addr, p, path,
                                                 names_addrs_dict[functionName],
                                                 allFunctionsStartAndEnd[hex(names_addrs_dict[functionName])])

            print
            functionName  # 打印函数名
            # 序号,函数名,基本块数量,数据流结点数量,控制流边数量,数据流边数量,程序名,版本编号
            function_str = str(func_num) + "," + str(functionName) + "," + \
                           str(cfg.functions[names_addrs_dict[functionName]].graph.number_of_nodes()) + "," + str(
                dfg_node_num) + "," + \
                           str(cfg_edge_num) + "," + str(dfg_edge_num) + "," + \
                           str(program) + "," + str(version) + "," + str(cfgtime - start) + "," + str(
                dfgtime - cfgtime) + "," + str(dfgtime - start) + ",\n"
            function_list_fp.write(function_str)


def extract_bin_block_fea(program, version, project, path):
    global bin_num, func_num, function_list_fp

    #  加载二进制文件
    p = angr.Project(project, load_options={'auto_load_libs': False})

    print
    p.filename  # 文件的全名
    print
    p.arch  # 架构
    # cfg_all = p.analyses.CFGAccurate(normalize=True,keep_state = True)
    # 找到所有函数
    for functionName in p.loader.main_object._symbols_by_name:
        # if not functionName == 'rpl_strtod':
        #     continue
        symbol = p.loader.main_object.get_symbol(functionName)
        # 位于.text中的函数
        if symbol.is_function:

            if not os.path.exists(path + os.sep + functionName + "_cfg.txt"):
                continue

            # if os.path.exists(path + os.sep + functionName + "_fea.csv"):
            #     continue

            # 生成控制流图
            start = time.time()
            # gaojian annotation
            # cfg = p.analyses.CFGAccurate(normalize=True, keep_state=True,starts=[symbol.rebased_addr])
            # gaojian add
            cfg = p.analyses.CFGFast(collect_data_references=False, resolve_indirect_jumps=False,
                                     force_complete_scan=False, function_starts=[symbol.rebased_addr])

            # 遍历所有的基本块,处理基本块内的反汇编代码,抽取基本块特征向量
            block_fea(p.filename, functionName, p, cfg, path)

            cfgtime = time.time()
            print
            functionName  # 打印函数名
            # 序号,函数名,基本块数量,数据流结点数量,控制流边数量,数据流边数量,程序名,版本编号
            function_str = str(func_num) + "," + str(functionName) + "," + \
                           str(program) + "," + str(version) + "," + str(cfgtime - start) + ",\n"
            function_list_fp.write(function_str)


# 转移指令
allTransferInstr = ['je', 'jz', 'jne', 'jnz', 'js', 'jns', 'jo', 'jno', 'jc', 'jnc', 'jp', 'jpe', 'jnp',
                    'jpo', 'jl', 'jpo', 'jl', 'jnge', 'jnl', 'jge', 'jg', 'jnle', 'jng', 'jb',
                    'jnae', 'jnb', 'jae', 'ja', 'jnbe', 'jna', 'jbe', 'jcxz',
                    'jecxz', 'reg', 'ops', 'bound', 'bound reg', 'int', 'into', 'iret', 'iretd',
                    'iretf', ]
#  堆栈指令
stackInstr = ['push', 'pop', 'pusha', 'popa', 'pushad', 'popad', 'pushf', 'popf', 'pushd', 'popd', 'stmfd', 'ldmfd',
              'stmfa', 'ldmfa', 'stmed', 'ldmed', 'stmea', 'ldmea', 'stm', 'ldm', 'ldp', 'stp']
# 算数指令
arithmeticInstr = ['xadd', 'aaa', 'aad', 'aam', 'aas', 'adc', 'add', 'addu', 'addiu', 'daa', 'dadd', 'adds', 'madd',
                   'addi', 'addiu',
                   'daddi', 'daddu', 'daddiu', 'dsub', 'dsubu', 'subu', 'abs', 'dabs', 'dneg', 'cneg', 'fadd', 'fsub',
                   'sub', 'subu',
                   'dnegu', 'negu', 'cbw', 'cdq', 'cwd', 'cwde', 'daa', 'das', 'dec', 'div', 'divo', 'udiv', 'fdiv',
                   'divu',
                   'divou', 'idiv', 'ddiv', 'ddivu', 'divu', 'dmul', 'dmulu', 'neg', 'sdiv', 'smulh', 'msub', 'mul',
                   'mulu',
                   'mulo', 'mulou', 'dmulo', 'dmulou', 'dmult', 'dmultu', 'mult', 'multu', 'imul', 'fmul', 'inc',
                   'nec', 'drem', 'dremu', 'rem', 'remu', 'mfhi', 'mflo', 'mthi', 'mtlo', 'subs',
                   'sbb', 'rsb', 'rsblt', 'sbc', 'rsc', 'c', 'r', 'mla', 'smull', 'smlal', 'umull', 'umulh', 'umlal']
# 逻辑指令
logicInstr = ['and', 'ands', 'andi', 'andeq', 'or', 'xor', 'not', 'eor', 'orr', 'teq', 'ori', 'nor', 'shl', 'sal',
              'shr', 'orn', 'rev', 'revh', 'revsh',
              'sar', 'rol', 'ror', 'rcl', 'rcr', 'lsl', 'lsr', 'asr', 'rrx', 'bic', 'movi', 'sxtb', 'sxth', 'sxtw',
              'uxtb', 'uxth', 'xori',
              'sll', 'sllv', 'srl', 'dsll', 'dsll32', 'dsrl', 'dsrl32', 'dsra', 'dsra32', 'dsllv', 'dsrlv', 'dsrav',
              'sra', 'srav', 'srl', 'srlv']  # 逻辑指令
segInstr = ['bfc', 'bfi', 'bfxil', 'sbfiz', 'sbfx', 'ubfiz', 'bfm', 'sbfm', 'ubfm']
# 比较指令
compareInstr = ['test', 'tst', 'cmpxchg', 'cmp', 'cmn', 'fcmp', 'fcmpz', 'fcmpez', 'fcmpe', 'ccmp',
                'csel', 'cset', 'csetm', 'cinc', 'cinv', 'csinc', 'csinv', 'slt', 'slti', 'sltu', 'sltui', 'sltiu']
# 调用
externalInstr = ['blx', 'bx', 'call', 'callq', 'bl', 'bal']
internalInstr = []
# 条件跳转
conditionJumpInstr = ['jle', 'loop', 'loopw', 'loopd', 'loope', 'loope', 'loopz', 'loopne', 'loopnz',
                      'jcxz', 'jecxz', 'tbz', 'tbnz', 'cbz', 'cbnz', 'bls', 'blo', 'bhi', 'bhs', 'bcc', 'bmi', 'bvs',
                      'bpl',
                      'b.hi', 'b.eq', 'b.le', 'b.ne', 'b.cs', 'b.hs', 'b.cc', 'b.lo', 'b.mi', 'b.pl', 'b.vs', 'b.vc',
                      'b.ls', 'b.ge',
                      'b.lt', 'b.gt', 'b.al', 'beqz', 'beq', 'bnez', 'jalr', 'jal', 'bne', 'bnel', 'bnez', 'bnezl',
                      'beq1', 'beqz1',
                      'blez', 'blezl', 'blt', 'blt1', 'bltu', 'bltu1', 'bltz', 'bltza1', 'bltza11', 'ble', 'ble1',
                      'bleu', 'bleu1',
                      'bc0f', 'bc0f1', 'bc0t', 'bc0t1', 'bc2f', 'bc2f1', 'bc2t', 'bc2t1', 'bc1f', 'bc1f1', 'bc1t',
                      'bc1t1', 'bgeza11',
                      'bge', 'bge1', 'bgeu', 'bgeu1', 'bgez', 'bgez1', 'bgt', 'bgt1', 'bgtu', 'bgtu1', 'bgtz', 'bgtz1',
                      'bgeza1']
# 非条件跳转
unconditionJumpInstr = ['jmp', 'ret', 'retn', 'b', 'br', 'blr', 'j', 'jr', 'eret', 'bal']
# 基本指令
genericInstr = ['mov', 'move', 'movz', 'movn', 'movne', 'movk', 'fmov', 'mvn', 'movsx', 'movzx',
                'movge', 'movlt', 'moveq', 'movlo', 'movhs', 'movgt', 'movle', 'movhi', 'movls',
                'bswap', 'xchg', 'xlat', 'trap', 'in', 'out', 'lea', 'lfs', 'lds', 'lgs',
                'lw', 'lh', 'lb', 'ld', 'lwu', 'lhu', 'lbu', 'sb', 'sh', 'sd', 'sw', 'swr', 'swl', 'lui',
                'lwr', 'lwl', 'sdr', 'sdl', 'ldl', 'ldr', 'ldrls', 'ldrd',
                'lss', 'lahf', 'sahf', 'ldrb', 'ldrp', 'ldrh', 'ldrsh', 'ldrsw', 'ldrsb', 'nop', 'adr', 'adrl', 'adrp',
                'adrh',
                'str', 'strb', 'strd', 'strp', 'strh', 'swp', 'swpb', 'ldur', 'ldurb', 'ldursb', 'ldurh', 'ldursh',
                'ldursw',
                'ldtr', 'ldtrb', 'ldtrsb', 'ldtrh', 'ldtrsh', 'ldtrsw', 'stur', 'sturb', 'sturh',
                'ucvtf', 'fcvtzu', 'scvtf',
                'bfc', 'bfi', 'bfxil', 'sbfiz', 'sbfx', 'ubfiz', 'bfm', 'sbfm', 'ubfm', 'ubfx']

'''
抽取一个函数中各个基本块的特征
每个函数保存成一个CSV文件, 命名方式:[函数名_fea.csv]
#   堆栈、算术、逻辑、比较、外部调用、内部调用、条件跳转、非条件跳转、普通指令
'''


def block_fea(filename, functionName, project, cfg, path):
    global stackInstr, arithmeticInstr, logicInstr, compareInstr, externalInstr, internalInstr, \
        conditionJumpInstr, unconditionJumpInstr, genericInstr
    f = path + os.sep + functionName + "_fea.csv"
    fp = open(f, 'w')  # a 追加
    # print "遍历函数所有指令:"
    # idfer = project.analyses.Identifier()
    # for addr, symbol in idfer.run():
    #     print hex(addr), symbol
    for cur_node in cfg.nodes():
        StackNum = 0  # stackInstr
        MathNum = 0  # arithmeticInstr
        LogicNum = 0  # logicInstr
        CompareNum = 0  # compareInstr
        ExCallNum = 0  # externalInstr
        InCallNum = 0  # internalInstr
        ConJumpNum = 0  # conditionJumpInstr
        UnConJumpNum = 0  # unconditionJumpInstr
        GeneicNum = 0  # genericInstr
        fea_str = str(hex(cur_node.addr)) + ","
        print
        functionName, hex(cur_node.addr)
        # if hex(cur_node.addr)=='0x0':
        #     continue
        block = project.factory.block(cur_node.addr)
        # print block.pp()# 循环输出所有指令
        for inst in block.capstone.insns:
            if inst.insn.mnemonic in stackInstr:
                StackNum = StackNum + 1
            elif inst.insn.mnemonic in arithmeticInstr:
                MathNum = MathNum + 1
            elif inst.insn.mnemonic in logicInstr:
                LogicNum = LogicNum + 1
            elif inst.insn.mnemonic in compareInstr:
                CompareNum = CompareNum + 1
            elif inst.insn.mnemonic in externalInstr:
                ExCallNum = ExCallNum + 1
            elif inst.insn.mnemonic in internalInstr:
                InCallNum = InCallNum + 1
            elif inst.insn.mnemonic in conditionJumpInstr:
                ConJumpNum = ConJumpNum + 1
            elif inst.insn.mnemonic in unconditionJumpInstr:
                UnConJumpNum = UnConJumpNum + 1
            elif inst.insn.mnemonic in genericInstr:
                GeneicNum = GeneicNum + 1
            else:
                print
                "+++++++++", inst.insn.mnemonic,
        fea_str = fea_str + str(StackNum) + "," + str(MathNum) + "," + str(LogicNum) + "," + str(CompareNum) + "," \
                  + str(ExCallNum) + "," + str(ConJumpNum) + "," + str(UnConJumpNum) + "," + str(GeneicNum) + ",\n"
        fp.write(fea_str)


# + str(InCallNum) + ","
'''
生成CFG邻接表
每个txt中存放一个函数的控制流图, 命名方式:[函数名_cfg.txt]
# a b c  # a-b a-c
# d e  # d-e
# G = nx.read_adjlist(‘test.adjlist’)
'''


def gen_cfg(filename, functionName, cfg, path, addr, endAddr):
    count = 0
    f = path + os.sep + functionName + "_cfg.txt"
    fp = open(f, 'w')  # a 追加
    # print "CFG节点数:", cfg.graph.number_of_nodes()
    # for cfg_node in cfg.graph.nodes():
    print
    "addr:", hex(addr)
    print
    "functionName:", functionName
    for cfg_node in cfg.functions[addr].graph.nodes():
        # print hex(cfg_node.addr),
        cfg_str = str(hex(cfg_node.addr))
        for edge in cfg_node.successors():
            if hex(edge.addr) < hex(addr) or hex(edge.addr) > endAddr:
                continue
            else:
                count = count + 1
                cfg_str = cfg_str + " " + str(hex(edge.addr))
            # print hex(edge.addr),
            # print hex(cfg_node.addr, create_using=nx.DiGraph()),"---->",hex(edge.addr)  # 遍历所有边
        cfg_str = cfg_str + "\n"
        fp.write(cfg_str)

    return count


'''
生成DFG邻接表
每个txt中存放一个函数的数据流图, 命名方式:[函数名_dfg.txt]
# a b c  # a-b a-c
# d e  # d-e
# G = nx.read_adjlist(‘test.adjlist’)
'''


def gen_dfg(cfg_edge_num, functionName, cfg, start, p, path, addr, endAddr):
    f = path + os.sep + functionName + "_dfg.txt"
    fp = open(f, 'w')  # a 追加
    try:
        vfg = p.analyses.VFG(cfg=cfg, start=start)
    except:
        return 0, 0
    dfg_node_num = vfg.graph.number_of_nodes()
    count = 0
    # 遍历数据流边
    # print "DFG节点数:", vfg.graph.number_of_nodes()
    for vfg_node in vfg.graph.nodes():
        dfg_str = ""
        if len(vfg_node.final_states) == 0:
            dfg_node_num = dfg_node_num - 1
            continue
        # print hex(vfg_node.addr),
        dfg_str = str(hex(vfg_node.addr))
        for edge in vfg_node.final_states:
            if hex(edge.addr) < hex(addr) or hex(edge.addr) > endAddr:
                continue
            else:
                count = count + 1
                dfg_str = dfg_str + " " + str(hex(edge.addr))
            # print hex(edge.addr),
            # print hex(cfg_node.addr),"---->",hex(edge.addr)  # 遍历所有边
        dfg_str = dfg_str + "\n"

        if not dfg_str.strip() == "":
            fp.write(dfg_str)

    return dfg_node_num, count


def main(versionname):
    global bin_num, func_num, function_list_file, function_list_fp
    flag = False

    if not os.path.exists(fea_dir):
        os.mkdir(fea_dir)

    # 遍历所有工程
    for program in programlist:
        fea_program_dir = fea_dir + os.sep + str(program)
        if not os.path.exists(fea_program_dir):
            os.mkdir(fea_program_dir)

        # 遍历所有编译版本
        func_num = 0
        version=versionname
        fea_program_version_dir = fea_program_dir + str(os.sep) + str(version)
        cur_bin_dir = bin_dir + os.sep + str(program) + str(os.sep) + str(version)
        if not os.path.exists(fea_program_version_dir):
            os.mkdir(fea_program_version_dir)
        function_list_file = fea_program_version_dir + os.sep + "functions_list.csv"
        function_list_fp = open(function_list_file, 'a')  # a 追加

        # filters = glob.glob(cur_bin_dir+ str(os.sep)  + "*.o")
        filters = glob.glob(cur_bin_dir + str(os.sep) + "*")
        bin_num = 0
        # 遍历这一版本中的所有二进制文件，导出到一个文件中
        for project in filters:
            if (".sh" in project):
                continue
            extract_bin_fea(program, version, project, fea_program_version_dir)
            bin_num = bin_num + 1


        # func_num = 0
        # for version in os.listdir(bin_dir + os.sep + str(program)):
        #     if (version == "DES-1210-28_REVA_FIRMWARE_2.00.011"):
        #         flag = True
        #     if (flag):
        #         fea_program_version_dir = fea_program_dir + str(os.sep) + str(version)
        #         cur_bin_dir = bin_dir + os.sep + str(program) + str(os.sep) + str(version)
        #         if not os.path.exists(fea_program_version_dir):
        #             os.mkdir(fea_program_version_dir)
        #         function_list_file = fea_program_version_dir + os.sep + "functions_list.csv"
        #         function_list_fp = open(function_list_file, 'a')  # a 追加
        #
        #         # filters = glob.glob(cur_bin_dir+ str(os.sep)  + "*.o")
        #         filters = glob.glob(cur_bin_dir + str(os.sep) + "*")
        #         bin_num = 0
        #         # 遍历这一版本中的所有二进制文件，导出到一个文件中
        #         for project in filters:
        #             if (".sh" in project):
        #                 continue
        #             if (".so" in project):
        #                 continue
        #             if (".ko" in project):
        #                 continue
        #             extract_bin_fea(program, version, project, fea_program_version_dir)
        #             bin_num = bin_num + 1


def fea_main():
    global bin_num, func_num, function_list_file, function_list_fp

    if not os.path.exists(fea_dir):
        os.mkdir(fea_dir)

    # 遍历所有工程
    for program in programlist:
        fea_program_dir = fea_dir + os.sep + str(program)
        if not os.path.exists(fea_program_dir):
            os.mkdir(fea_program_dir)

        # 遍历所有编译版本
        func_num = 0
        for version in os.listdir(bin_dir + os.sep + str(program)):
            if (version == "121219"):
                continue
            fea_program_version_dir = fea_program_dir + str(os.sep) + str(version)
            cur_bin_dir = bin_dir + os.sep + str(program) + str(os.sep) + str(version)
            if not os.path.exists(fea_program_version_dir):
                os.mkdir(fea_program_version_dir)
            function_list_file = fea_program_version_dir + os.sep + "functions_list_fea.csv"
            function_list_fp = open(function_list_file, 'w')  # a 追加

            # filters = glob.glob(cur_bin_dir+ str(os.sep)  + "*.o")
            filters = glob.glob(cur_bin_dir + str(os.sep) + "*")
            bin_num = 0
            # 遍历这一版本中的所有二进制文件，导出到一个文件中
            for project in filters:
                print
                project
                if (".sh" in project):
                    continue
                extract_bin_block_fea(program, version, project, fea_program_version_dir)
                bin_num = bin_num + 1


if __name__ == "__main__":
    # fea_main()
    script, first = argv
    main(first)

# if __name__ == "__main__":
#     main()

# a b c  # a-b a-c
# d e  # d-e
# G = nx.read_adjlist(‘test.adjlist’)


# cdg = p.analyses.CDG(cfg, start=symbol.rebased_addr)
# print "CDG节点数:", cdg.graph.number_of_nodes()
# for cfg_node in cdg.graph.nodes():
#     print hex(cfg_node.addr),
#     for edge in cfg_node.successors:
#         print hex(edge.addr),
#         # print hex(cfg_node.addr),"---->",hex(edge.addr)  # 遍历所有边
#     print ""

# print "DFG节点数:", vfg.graph.number_of_nodes()
# for dfg_node in vfg.graph.nodes():
#     print hex(dfg_node.addr),
#     for edge in dfg_node.successors:
#         print hex(edge.addr),
#         # print hex(cfg_node.addr),"---->",hex(edge.addr)  # 遍历所有边
#     print ""
#     # a b c  # a-b a-c
#     # d e  # d-e
#     # G = nx.read_adjlist(‘test.adjlist’)

# dfg = p.analyses.VSA_DDG(start_addr=symbol.rebased_addr)
# dfg = p.analyses.DDG(cfg=cfg,start=symbol.rebased_addr)
#
# for dfg_node  in dfg.graph.nodes():
#     print hex(dfg_node.addr),
#     for edge in dfg_node.successors:
#         print hex(edge.addr),
#     print ""


# def analyze(b, addr, name=None):
#     start_state = b.factory.blank_state(addr=addr)
#     start_state.stack_push(0x0)
#     cfg = b.analyses.CFGAccurate(fail_fast=True, starts=[addr], initial_state=start_state, context_sensitivity_level=2,
#                                  keep_state=True, call_depth=100, normalize=True)
#
#     plot_cfg(cfg, "%s_cfg" % (name), asminst=True, vexinst=True, debug_info=False, remove_imports=True,
#              remove_path_terminator=True)
#
#     dfg = b.analyses.DFG(cfg=cfg)
#     for a, g in dfg.dfgs.iteritems():
#         plot_dfg(g, "%s_dfg_%x" % (name, a))
#
#
# if __name__ == "__main__":
#     proj = angr.Project("ca.o", load_options={'auto_load_libs': False})
#     main = proj.loader.main_bin.get_symbol("main")
#     analyze(proj, main.addr, "simple1")


# print hex(p.entry)  # 二进制文件的入口点
# print hex(p.loader.min_addr)  # 二进制文件内存空间中的最小地址
# print hex(p.loader.max_addr)  # 二进制文件内存空间中的最大地址
# print p.load_function.func_dict
# .loader.main_object
# 生成控制流图
# cfg = p.analyses.CFGAccurate(keep_state = True)
# print "cfg"
# nx.draw(cfg.graph)
# plt.show()

# cdg = p.analyses.CDG(cfg)
# print "cdg"
# ddg = p.analyses.DDG(cfg)
# print "ddg"


# bs = angr.analyses.backward_slice.BackwardSlice(cfg, cdg=cdg, ddg=ddg, targets=[ (target_node, -1) ])
# annocfg = bs.annotated_cfg(symbol.rebased_addr)
# d = p.analyses.DFG(cfg, annocfg)


# target_func = cfg.kb.functions.function(name=functionName)
# target_func = cfg.kb.functions[symbol.rebased_addr]
# print target_func.graph.number_of_nodes()
# print target_func.graph.nodes()
# print entry_func.graph.edges()

# SimProcedure.
# target_func = cfg.kb.functions.function(name=entry_func.name)
# We need the CFGNode instance
# target_node = cfg.get_any_node(target_func.addr)
# block = p.factory.block(symbol.rebased_addr)


# angr.analyses.backward_slice.BackwardSlice(cfg, cdg, ddg)

# p.analyses.DDGView(cfg, ddg, simplified=False)
# cfg = f.analyses.CFGAccurate(keep_state = True)
# print len(cfg.graph.nodes()),len(cfg.graph.edges())
# annocfg = p.analyses.BackwardSlice(cfg, cdg, ddg)
# print "annocfg"
# d = p.analyses.DFG(cfg=cfg,annocfg=annocfg)
# print d
# print len(d.graph.nodes()),len(d.graph.edges())
#
# bbl_addr, dfg = d.dfgs.popitem()
# print(bbl_addr)
# print(dfg.in_edges())

# idfer = p.analyses.Identifier(keep_state = True)
# for addr, symbol in idfer.run():
#     print hex(addr), symbol


# class DFG(Analysis):
#     def __init__(self, cfg=None, annocfg=None):
#             if dfg.size() > 0:
#                 dfgs[node.addr] = dfg
#         return dfgs

# ===================================
# function_dependency_graph(func)
# Get a dependency graph for the function func.
#
# Parameters:	func – The Function object in CFG.function_manager.
# Returns:	A networkx.DiGraph instance.
